package com.digitalmoney.msaccounts.config.security;

import com.digitalmoney.msaccounts.config.filters.LoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(new LoggingFilter(), UsernamePasswordAuthenticationFilter.class);

        http
                .csrf().disable()
            .authorizeHttpRequests((requests) -> requests
                    .requestMatchers(HttpMethod.GET, "/accounts/test-db").permitAll()
                    .requestMatchers(HttpMethod.POST, "/accounts/create").permitAll()
                    .requestMatchers(HttpMethod.GET, "accounts/{accountId}/cards").permitAll()
                    .requestMatchers(HttpMethod.GET, "accounts/{accountId}/cards/{cardId}").permitAll()
                    .requestMatchers(HttpMethod.GET, "/error").permitAll()
                    .anyRequest().permitAll()
            );

        return http.build();
    }}
