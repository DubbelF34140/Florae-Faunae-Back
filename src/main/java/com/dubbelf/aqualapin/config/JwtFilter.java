package com.dubbelf.aqualapin.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtils jwtUtils;
    private final MyUserDetailsService userDetailsService;

    public JwtFilter(JwtUtils jwtUtils, MyUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // Log pour débogage - désactivez en production
        logger.debug("Processing request: {} {}", method, requestURI);

        try {
            String jwt = parseJwt(request);

            if (jwt != null) {
                logger.debug("JWT token found in request to: {}", requestURI);

                if (jwtUtils.validateJwtToken(jwt)) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);
                    logger.debug("Valid JWT token for user: {} accessing: {}", username, requestURI);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Authentication set successfully for user: {}", username);
                } else {
                    logger.warn("Invalid JWT token for request: {}", requestURI);
                }
            } else {
                logger.debug("No JWT token found in request to: {}", requestURI);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication for request {}: {}", requestURI, e.getMessage(), e);
            // Important: ne pas retourner d'erreur ici, laisser Spring Security gérer
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        logger.debug("Authorization header: {}", headerAuth != null ? "Bearer ***" : "null");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
