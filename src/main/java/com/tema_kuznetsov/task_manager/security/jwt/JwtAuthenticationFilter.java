package com.tema_kuznetsov.task_manager.security.jwt;

import com.tema_kuznetsov.task_manager.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("Authorization header is missing or does not start with 'Bearer '");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7).replaceAll("\\s+", "");
        logger.debug("Extracted JWT: {}", jwt);

        try {
            userEmail = jwtService.extractUsername(jwt);
            logger.debug("Extracted username from token: {}", userEmail);
        } catch (Exception e) {
            logger.warn("Failed to extract username from token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                logger.debug("Loaded user details: {}", userDetails.getUsername());
                logger.debug("User authorities: {}", userDetails.getAuthorities());

                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    logger.debug("Token is valid for user: {}", userEmail);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("Token is invalid for user: {}", userEmail);
                }

            } catch (UsernameNotFoundException ex) {
                logger.warn("JWT refers to non-existent user: {}", userEmail);
            }
        } else {
            logger.debug("User is already authenticated or username is null");
        }

        filterChain.doFilter(request, response);
    }
}