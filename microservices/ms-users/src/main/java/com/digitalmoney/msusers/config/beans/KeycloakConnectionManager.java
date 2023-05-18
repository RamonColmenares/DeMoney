package com.digitalmoney.msusers.config.beans;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakConnectionManager {
    @Value("${app.keycloak.login.url}")
    public String loginUrl;
    @Value("${keycloak.auth-server-url}")
    public String keycloakAuthUrl;
    @Value("${app.keycloak.client-secret}")
    public String clientSecret;
    @Value("${app.keycloak.client-id}")
    public String clientId;
    @Value("${keycloak.realm}")
    public String realm;
    @Value("${keycloak.username}")
    public String username;
    @Value("${keycloak.password}")
    public String password;


    public Keycloak getConnectionAdmin() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl) //localhost:8080/auth
                .realm(realm) // usually it's master
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build();
    }

    public AccessTokenResponse getConnectionUser(String username, String password) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .realm(realm) // usually it's master
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build();
        return keycloak.tokenManager().getAccessToken();
    }

    public Keycloak getConnectionService() {
        return  KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .realm(realm) // usually it's master
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    public Keycloak getConnectionWithToken(String token) {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .realm(realm)
                .grantType(OAuth2Constants.AUTHORIZATION_CODE)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorization(token)
                .build();
    }
}
