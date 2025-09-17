package com.dubbelf.aqualapin.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dubbelf.aqualapin.entity.User;
import com.dubbelf.aqualapin.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Component
public class JwtUtils {

    @Autowired
    private final UserRepository userRepository;

    private final String jwtSecret = "1604Y@kumo6798";

    public JwtUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    public UUID getIDFromJwtToken(String token) {
        String username = JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(token).getClaim("username").toString().replaceAll("\"", "");
        User user = userRepository.findBypseudo(username);
        return  user.getId();
    }

    public String generateJwtToken(Authentication authentication) {
        UtilisateurSpringSecurity userPrincipal = (UtilisateurSpringSecurity) authentication.getPrincipal();
        List<String> roles = (List<String>) userPrincipal.getRoles();

        return JWT.create()
                .withClaim("username", userPrincipal.getUsername())
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
                .build()
                .verify(token);
        return decodedJWT.getClaim("username").asString();
    }

    public List<String> getRolesFromJwtToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
                .build()
                .verify(token);
        return decodedJWT.getClaim("roles").asList(String.class);
    }

    public boolean isAdmin(String token) {
        List<String> roles = getRolesFromJwtToken(token);
        return roles != null && roles.contains("ROLE_ADMIN");
    }

    public boolean validateJwtToken(String authToken) {
        try {
            JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(authToken);
            return true;
        } catch (Exception e) {
            System.out.println("JWT validation error: " + e.getMessage());
        }
        return false;
    }
}
