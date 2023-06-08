package com.digitalmoney.msaccounts.config.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JWTExtractionFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTExtractionFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String decompressedToken = request.getHeader("Authorization");
        LOGGER.debug(decompressedToken);
        if (decompressedToken != null) {
            // Parse the JWT token
            Map<String, Object> tokenBody = decodeJwt(request.getHeader("Authorization").replace("Bearer ", ""));
            LOGGER.debug(tokenBody.toString());

            // Extract roles
            Map<String, Map<String, List<String>>> resourceAccess = (Map<String, Map<String, List<String>>>) tokenBody.get("resource_access");
            List<String> roles = resourceAccess.get("account").get("roles");

            // Extract user information
            String id = (String) tokenBody.get("usedDBID");
            LOGGER.debug(id);

            // Here, I'll just use the preferredUsername as the principal and roles as the authorities for the authentication token.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(id, null, roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private Map<String, Object> decodeJwt(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaims().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, item -> item.getValue().as(Object.class)));
    }
}
