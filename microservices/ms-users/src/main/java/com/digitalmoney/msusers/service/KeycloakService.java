package com.digitalmoney.msusers.service;

import com.digitalmoney.msusers.application.dto.UserLoginResponseDTO;
import com.digitalmoney.msusers.application.dto.UserRegisterDTO;
import com.digitalmoney.msusers.application.dto.UserUpdateDTO;
import com.digitalmoney.msusers.config.beans.KeycloakConnectionManager;
import com.digitalmoney.msusers.config.security.TokenProvider;
import lombok.extern.log4j.Log4j2;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service @Log4j2
public class KeycloakService {
    private final KeycloakConnectionManager keycloakConnectionManager;
    private final String keycloakBaseRestURI;

    public KeycloakService(KeycloakConnectionManager keycloakConnectionManager) {
        this.keycloakConnectionManager = keycloakConnectionManager;
        keycloakBaseRestURI = keycloakConnectionManager.getKeycloakAuthUrl() + "/realms/" + keycloakConnectionManager.getRealm();
    }

    public Response createInKeycloak(UserRegisterDTO user) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(user.password());
        credential.setTemporary(false);

        UserRepresentation userDB = new UserRepresentation();
        userDB.setEnabled(true);
        userDB.setUsername(user.email());
        userDB.setFirstName(user.firstName());
        userDB.setLastName(user.lastName());
        userDB.setEmail(user.email());
        userDB.setCredentials(singletonList(credential));
        userDB.setRealmRoles(singletonList("user"));


        return keycloakConnectionManager.getConnectionAdmin().realm("users-bank").users().create(userDB);
    }

    public Response updateInKeycloak(String email, UserUpdateDTO user) {
        String userId = getUserIdByEmail(email);

        if (userId == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        UserResource userResource = keycloakConnectionManager.getConnectionAdmin()
                .realm("users-bank")
                .users()
                .get(userId);

        UserRepresentation userRepresentation = userResource.toRepresentation();

        if (user.password() != null) {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(user.password());
            credential.setTemporary(false);

            List<CredentialRepresentation> credentials = userRepresentation.getCredentials();
            if (credentials == null) {
                credentials = new ArrayList<>();
            } else {
                // Remove existing password credentials
                credentials.removeIf(cred -> cred.getType().equals(CredentialRepresentation.PASSWORD));
            }
            credentials.add(credential);
            userRepresentation.setCredentials(credentials);
        }

        if (user.firstName() != null) {
            userRepresentation.setFirstName(user.firstName());
        }

        if (user.lastName() != null) {
            userRepresentation.setLastName(user.lastName());
        }

        if (user.email() != null) {
            UserRepresentation newUserRepresentation = new UserRepresentation();
            newUserRepresentation.setUsername(user.email());
            newUserRepresentation.setEmail(user.email());
            newUserRepresentation.setFirstName(userRepresentation.getFirstName());
            newUserRepresentation.setLastName(userRepresentation.getLastName());
            newUserRepresentation.setEnabled(userRepresentation.isEnabled());
            newUserRepresentation.setRealmRoles(userRepresentation.getRealmRoles());
            newUserRepresentation.setAttributes(userRepresentation.getAttributes());

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(user.password());
            credential.setTemporary(false);

            newUserRepresentation.setCredentials(singletonList(credential));

            // create the new user with the new username
            Response response = keycloakConnectionManager.getConnectionAdmin()
                    .realm("users-bank")
                    .users()
                    .create(newUserRepresentation);

            if (response.getStatus() == 201) {
                // new user created correctly, now we have to delete old one
                userResource.remove();
                return Response.ok().build();
            } else {
                // an error has ocurred while creating the new user
                return Response.serverError().build();
            }
        }

        userResource.update(userRepresentation);
        return Response.ok().build();
    }

    public List<UserRepresentation> test(String username) {
        return keycloakConnectionManager.getConnectionAdmin().realm("users-bank").users().search(username, true);
    }

    public void removeFromKeycloak(UserRegisterDTO user) {
        try {
            String id = keycloakConnectionManager.getConnectionAdmin().realm("users-bank").users().search(user.email(), true).get(0).getId();
            keycloakConnectionManager.getConnectionAdmin().realm("users-bank").users().delete(id);
        } catch (Exception e) {
            log.error("Error deleting faulty user: ", e);
        }
    }

    public UserLoginResponseDTO userLogin(String username, String password) throws NotAuthorizedException {
        TokenManager kToken = keycloakConnectionManager.getConnectionUser(username, password);
        return new UserLoginResponseDTO(kToken.getAccessToken().getToken(), kToken.getAccessToken().getRefreshToken());
    }

    public void logout(String token, String refreshToken) {
        MultiValueMap<String, String> requestParams = getValuesFromManager();
        requestParams.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);
        String url = keycloakBaseRestURI + "/protocol/openid-connect/logout";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<?> response = restTemplate.postForEntity(url, request, Object.class);
        if(response.getStatusCode().value() == 204) {
            TokenProvider.INVALID_TOKENS.add(token);
            cleanInvalidTokens(token);
        }
    }

    @Async
    protected void cleanInvalidTokens(String token) {
        log.info("Trying to invalidate token " + token);
        CompletableFuture.delayedExecutor(60, TimeUnit.SECONDS).execute(() -> {
            TokenProvider.INVALID_TOKENS.remove(token);
        });
    }

    public Object getGrants(String token) throws VerificationException {
        AccessToken accessToken = TokenVerifier.create(token, AccessToken.class).getToken();
        return accessToken.getRealmAccess().getRoles().stream().map(e -> "ROLE_" + e.toUpperCase()).collect(Collectors.toList());
    }

    private String getUserIdByEmail(String email) {
        List<UserRepresentation> users = keycloakConnectionManager.getConnectionAdmin()
                .realm("users-bank")
                .users()
                .search(email, true);

        if (!users.isEmpty()) {
            return users.get(0).getId();
        } else {
            return null;
        }
    }

    public boolean validateToken(String token) {
        /*Keycloak keycloak = keycloakConnectionManager.getConnectionAdmin().proxy();
        AdapterTokenVerifier
        keycloak.cert
        keycloak.close();*/
        return true;
    }

    public UserLoginResponseDTO refreshToken(String refreshToken) {
        MultiValueMap<String, String> requestParams = getValuesFromManager();
        requestParams.add("refresh_token", refreshToken);
        requestParams.add("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);
        String url = keycloakBaseRestURI + "/protocol/openid-connect/token";
        ResponseEntity<KeycloakTokenResponse> keycloakTokenResponseResponseEntity;
        try {
            keycloakTokenResponseResponseEntity = new RestTemplate().postForEntity(url, request, KeycloakTokenResponse.class);
        } catch (HttpClientErrorException.BadRequest e) {
            return null;
        }
        return new UserLoginResponseDTO(keycloakTokenResponseResponseEntity.getBody().access_token(), null);
    }

    private MultiValueMap<String, String> getValuesFromManager() {
        return keycloakConnectionManager.getRequestParamsWithClient();
    }

    public void logoutv2(String userId) {
        Keycloak keycloak = keycloakConnectionManager.getConnectionAdmin();
        keycloak.realm("Master").users().get(userId).logout();
        keycloak.close();
    }

    public void addDbUserId(Long id, String email) {
            String userId = getUserIdByEmail(email);

            UserResource userResource = keycloakConnectionManager.getConnectionAdmin()
                    .realm("users-bank")
                    .users()
                    .get(userId);

            UserRepresentation userRepresentation = userResource.toRepresentation();
            userRepresentation.setAttributes(new HashMap<>());
            userRepresentation.getAttributes().put("usedDBID", List.of(id.toString()));
            userResource.update(userRepresentation);
    }
}

record KeycloakTokenResponse(
        String access_token,
        int expires_in,
        int refresh_expires_in,
        String refresh_token,
        String token_type,
        int not_before_policy,
        String session_state,
        String scope
) {}