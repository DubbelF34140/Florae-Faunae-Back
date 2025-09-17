package com.dubbelf.aqualapin.controller;

import com.dubbelf.aqualapin.config.EncryptionUtil;
import com.dubbelf.aqualapin.config.JwtUtils;
import com.dubbelf.aqualapin.config.UtilisateurSpringSecurity;
import com.dubbelf.aqualapin.dto.JwtPayload;
import com.dubbelf.aqualapin.dto.LoginInput;
import com.dubbelf.aqualapin.dto.PasswordDto;
import com.dubbelf.aqualapin.dto.UserFormInput;
import com.dubbelf.aqualapin.entity.User;
import com.dubbelf.aqualapin.repository.UserRepository;
import com.dubbelf.aqualapin.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(
        origins = {"http://localhost:5173","http://localhost", "https://localhost", "capacitor://localhost" },
        allowCredentials = "true"
)
@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
    UserService us;

	@Autowired
    JwtUtils jwtUtils;

	@Autowired
    EncryptionUtil encryptionUtil;

    @Autowired
    UserRepository userRepository;


	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginInput loginInput) {
		loginInput.setPassword(encryptionUtil.decrypt(loginInput.getPassword()));
		loginInput.setpseudo(encryptionUtil.decrypt(loginInput.getpseudo()));

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginInput.getpseudo(), loginInput.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UtilisateurSpringSecurity userDetails = (UtilisateurSpringSecurity) authentication.getPrincipal();
		boolean admin = userDetails.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        User user = userRepository.findByIdObject(userDetails.getId());

		return ResponseEntity.ok(new JwtPayload(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getRoles().toString(),user.getAvatarUrl(), admin));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserFormInput userFormInput, BindingResult br) {
		Map<String, String> errors = new HashMap<>();

		if (br.hasErrors()) {
			for (FieldError error : br.getFieldErrors()) {
				errors.put(error.getField(), error.getDefaultMessage());
			}
			return ResponseEntity.badRequest().body(errors);
		}

		errors = us.registerUser(userFormInput, errors);

		if (!errors.isEmpty()) {
			return ResponseEntity
					.badRequest()
					.body(errors);
		}

		return ResponseEntity.ok("User registered successfully!");
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<?> resetPassword(@RequestParam("email") String userEmail) {
		Map<String, String> errors = new HashMap<>();

		errors = us.resetPassword(userEmail, errors);

		if (errors.isEmpty()) {
			return ResponseEntity.ok("Email envoyé");
		} else {
			return ResponseEntity.badRequest().body(errors);
		}
	}

	@PostMapping("/savePassword")
	public ResponseEntity<?> savePassword(@Valid @RequestBody PasswordDto passwordDto) {
		Map<String, String> errors = new HashMap<>();

		errors = us.savePassword(passwordDto, errors);

		if (!errors.isEmpty()) {
			return ResponseEntity
					.badRequest()
					.body(errors);
		}

		return ResponseEntity.ok("Password reset successfully!");
	}
}
