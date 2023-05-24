package com.digitalmoney.msusers.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public final static String AUTHORIZATION_HEADER = "Authorization";

    private final TokenProvider tokenProvider;

    @Autowired
    public SecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JWTFilter customFilter = new JWTFilter(this.tokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new LoggingFilter(), JWTFilter.class);


        http
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.GET, "/ping").permitAll()
                        .requestMatchers(HttpMethod.GET, "/test-db").permitAll()
                        .requestMatchers(HttpMethod.GET, "/test-keycloak").permitAll()
                        .requestMatchers(HttpMethod.POST,"/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/me/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.GET, "/error").permitAll()
                        .anyRequest().authenticated()

                );

        return http.build();
    }
}
