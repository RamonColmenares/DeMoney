package com.digitalmoney.msusers.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingFilter extends GenericFilterBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Before request logging
        String method = request.getMethod();
        String endpoint = request.getRequestURI();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username;
        if (authentication == null) {
            username = "";
        } else {
            username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        }

        String ip = request.getRemoteAddr();
        LOGGER.info("Before request [{} {}] [{}] {}", method, endpoint, username, ip);

        filterChain.doFilter(servletRequest, servletResponse);

        // After request logging
        String responseStatus = String.valueOf(response.getStatus());
        LOGGER.info("After request [{} {}] [{}] [{}]", method, endpoint, username, responseStatus);
    }
}
