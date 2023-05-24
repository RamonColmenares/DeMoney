package com.digitalmoney.msusers.config.security;

import com.digitalmoney.msusers.config.beans.KeycloakConnectionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.jose.jwk.JWK;
import org.keycloak.jose.jwk.JWKParser;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);

    public static List<String> INVALID_TOKENS = new ArrayList<>();

    private final KeycloakConnectionManager keycloakConnectionManager;

    @Autowired
    public TokenProvider(KeycloakConnectionManager keycloakConnectionManager) {
        this.keycloakConnectionManager = keycloakConnectionManager;
    }

    public Authentication getAuthentication(String token) {

        AccessToken token1 = validateToken(token);

        Collection<? extends GrantedAuthority> authorities = token1.getRealmAccess().getRoles()
                .stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(token1.getPreferredUsername(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public AccessToken validateToken(String authToken) {
        try {
            PublicKey publicKey = getRealmPublicKey();
            TokenVerifier<AccessToken> tokenVerifier = TokenVerifier.create(authToken, AccessToken.class);
            return tokenVerifier.publicKey(publicKey).verify().getToken();
        } catch (VerificationException e) {
            LOGGER.debug("VerificationException: ", e);
            return null;
        }
    }

    private PublicKey getRealmPublicKey() {
        return retrievePublicKeyFromCertsEndpoint();
    }

    private PublicKey retrievePublicKeyFromCertsEndpoint() {

        try {
            ObjectMapper om = new ObjectMapper();
            JSONWebKeySet jwks = om.readValue(new URL(keycloakConnectionManager.getKeycloakAuthUrl() + "/realms/" + keycloakConnectionManager.getRealm() + "/protocol/openid-connect/certs").openStream(), JSONWebKeySet.class);
            JWK jwk = jwks.getKeys()[1];
            return JWKParser.create(jwk).toPublicKey();
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }

        return null;
    }

    public boolean isValid(String jwt) {
        if (INVALID_TOKENS.contains(jwt)) {
            return false;
        }
        return this.validateToken(jwt).isActive();
    }
}