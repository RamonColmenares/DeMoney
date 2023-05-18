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

    /* MANEJAR ERRORES MEJOR */
    public AccessTokenResponse getConnectionUser(String username, String password) throws Exception {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build();
        return keycloak.tokenManager().getAccessToken();
	
	/* 
	Y si no encuentra nada? 
	    Error? throws, no catch (maneja el service)
	    Null? retornar así
	
	*/
    }

    public Keycloak getConnectionService() {
        return  KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .realm(realm) // usually it's master
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    public Keycloak getConnectionWithToken() {
        return Keycloak.getInstance(
                keycloakAuthUrl,
                realm, // the realm
                null, // the username - we're not using these since we already have a token
                null, // the password
                clientId, // the client id
                clientSecret // the client secret
        );
    }
}
