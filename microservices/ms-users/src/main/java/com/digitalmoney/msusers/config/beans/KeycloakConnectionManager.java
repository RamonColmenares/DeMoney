package com.digitalmoney.msusers.config.beans;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

    public String getKeycloakAuthUrl() {
        return keycloakAuthUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRealm() {
        return realm;
    }

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
    public TokenManager getConnectionUser(String username, String password) throws Exception {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build();
        return keycloak.tokenManager();
	
	/* 
	Y si no encuentra nada? 
	    Error? throws, no catch (maneja el service)
	    Null? retornar así
	
	*/
    }
    public MultiValueMap<String, String> getRequestParamsWithClient() {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("client_id", clientId);
        requestParams.add("client_secret", clientSecret);
        return requestParams;
    }
}
