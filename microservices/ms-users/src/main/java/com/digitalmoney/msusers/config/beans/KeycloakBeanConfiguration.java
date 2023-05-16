package com.digitalmoney.msusers.config.beans;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;

@Configuration
public class KeycloakBeanConfiguration {
    @Value("${app.keycloak.login.url}")
    private String loginUrl;
    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthUrl;
    @Value("${app.keycloak.client-secret}")
    private String clientSecret;
    @Value("${app.keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.realm}")
    public String realm;
    @Value("${keycloak.username}")
    private String username;
    @Value("${keycloak.password}")
    private String password;

    @Bean
    @Profile({"dev", "prod"})
    public Keycloak getConnection() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .realm(realm) // usually it's master
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build();
    }

    @Bean
    @Profile("build")
    public Keycloak getConnectionBuild() {
        return KeycloakBuilder.builder()
                .serverUrl("localhost")
                .realm("master") // usually it's master
                .username("build")
                .password("build")
                .clientId("clientid")
                .build();
    }
}
