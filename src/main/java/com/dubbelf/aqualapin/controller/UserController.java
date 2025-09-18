package com.dubbelf.aqualapin.controller;

import com.dubbelf.aqualapin.config.JwtUtils;
import com.dubbelf.aqualapin.config.MyUserDetailsService;
import com.dubbelf.aqualapin.config.UtilisateurSpringSecurity;
import com.dubbelf.aqualapin.dto.AdminChangeRoleUser;
import com.dubbelf.aqualapin.dto.EditUser;
import com.dubbelf.aqualapin.dto.ProfileDTO;
import com.dubbelf.aqualapin.dto.UserDTO;
import com.dubbelf.aqualapin.entity.User;
import com.dubbelf.aqualapin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dubbelf.aqualapin.config.JwtUtils.parseJwt;

@RestController
@RequestMapping("/user")
@CrossOrigin(
        origins = { "http://localhost:5173", "https://localhost", "capacitor://localhost" },
        allowCredentials = "true"
)
public class UserController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsers() {
        UtilisateurSpringSecurity userDetails =
                (UtilisateurSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            List<UserDTO> utilisateurs = userService.getUsers().stream()
                    .map(UserDTO::new)
                    .toList();
            return ResponseEntity.ok(utilisateurs);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/admin/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> ChangeRoleUser(@RequestBody AdminChangeRoleUser  adminChangeRoleUser, HttpServletRequest httpRequest) {
        String jwtToken = parseJwt(httpRequest);
        if (jwtToken != null) {
            if (jwtUtils.isAdmin(jwtToken)) {
                userService.ChangeroleUser(adminChangeRoleUser);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.ok(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/edit")
    public ResponseEntity<String> EditUser(@RequestBody EditUser user) {
        try {
            userService.EditUser(user);
            return ResponseEntity.ok("Modifictation de l'utilisateur avec succès");
        } catch (Exception error) {
            return ResponseEntity.badRequest().body("Erreur lors de la modifictation de l'utilisateur : " + error.getMessage());
        }
    }


    @GetMapping("/profil/{pseudo}")
    public ResponseEntity<ProfileDTO> getUsersProfile(@PathVariable String pseudo, HttpServletRequest httpRequest) {
        String jwtToken = parseJwt(httpRequest);
        if (jwtToken != null) {
            UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
            return userService.GetProfileUser(pseudo, currentUserId);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/sub/{pseudo}")
    public ResponseEntity<?> subUsersProfile(@PathVariable String pseudo, HttpServletRequest httpRequest) {
        String jwtToken = parseJwt(httpRequest);
        if (jwtToken != null) {
            UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
            return userService.SubProfileUser(pseudo, currentUserId);
        }
        return ResponseEntity.badRequest().body("Erreur lors de l'abbonement à l'utilisateur");

    }

    @GetMapping("/subs")
    public ResponseEntity<List<UserDTO>> getMySubscriptions(HttpServletRequest httpRequest) {
        String jwtToken = JwtUtils.parseJwt(httpRequest);
        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
        return userService.GetSubUser(currentUserId);
    }

    @PostMapping("/unsub/{pseudo}")
    public ResponseEntity<?> unsubUsersProfile(@PathVariable(value = "pseudo") String pseudo,
                                               HttpServletRequest httpRequest) {
        String decodedPseudo = java.net.URLDecoder.decode(pseudo, java.nio.charset.StandardCharsets.UTF_8);
        String jwtToken = parseJwt(httpRequest);
        if (jwtToken != null) {
            UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
            return userService.UnSubProfileUser(decodedPseudo, currentUserId);
        }
        return ResponseEntity.badRequest().body("Erreur lors du désabonnement à l'utilisateur");
    }
}
