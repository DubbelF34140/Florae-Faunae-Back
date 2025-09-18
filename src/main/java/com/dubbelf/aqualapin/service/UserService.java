package com.dubbelf.aqualapin.service;

import com.dubbelf.aqualapin.dto.*;
import com.dubbelf.aqualapin.entity.PasswordResetToken;
import com.dubbelf.aqualapin.entity.Role;
import com.dubbelf.aqualapin.entity.User;
import com.dubbelf.aqualapin.repository.PasswordTokenRepository;
import com.dubbelf.aqualapin.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    PasswordTokenRepository ptr;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    SecurityServiceImpl securityService;

    @Value("${spring.mail.username}")
    private String emailapp;
    @Autowired
    private PostService postService;

    public void addUser(UserFormInput userForm) {
        User user = new User();
        user.setEmail(userForm.getEmail());
        user.setPseudo(userForm.getPseudo());
        user.setRole(Role.USER);
        user.setMotDePasse(userForm.getPassword());
        user.setMotDePasse(encoder.encode(user.getMotDePasse()));

        userRepository.save(user);
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Cacheable(value = "users", key = "#id.toString()")
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    @Cacheable(value = "users", key = "#pseudo")
    public User getUserByUsernameOrEmail(String pseudo) {
        User user = userRepository.findBypseudo(pseudo);
        if (user == null) {
            user = userRepository.findByEmail(pseudo);
        }
        return user;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "users", key = "#id.toString()")
    public Optional<User> getUserById(UUID id) {
        User user = userRepository.findByIdObject(id);

        if (user == null) {
            return Optional.empty();
        }
        userRepository.save(user);

        return Optional.of(user);
    }

    @Cacheable(value = "users", key = "#id.toString()")
    public User getUserObjectById(UUID id) {
        return userRepository.findByIdObject(id);
    }

    public Map<String, String> registerUser(UserFormInput userForm, Map<String, String> errors) {

        errors = checkUserForm(userForm, errors);

        if (errors.isEmpty()) {
            addUser(userForm);
        }

        return errors;
    }

    public Map<String, String> checkUserForm(UserFormInput userForm, Map<String, String> errors) {
        if (usernameAlreadyExist(userForm.getPseudo())) {
            errors.put("username", "Username is already taken!");
        }

        if (emailAlreadyExist(userForm.getEmail())) {
            errors.put("email", "Email is already in use!");
        }

        errors = checkPassword(userForm.getPassword(), userForm.getPasswordConfirmation(), errors);

        return errors;
    }

    public boolean usernameAlreadyExist(String Pseudo) {
        return userRepository.existsByPseudo(Pseudo);
    }

    public boolean emailAlreadyExist( String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isValidOldPassword(String oldPassword, String actualPassword) {
        return encoder.matches(oldPassword, actualPassword);
    }


    public boolean isValidPassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    public Map<String, String> checkPassword(String password, String passwordConfirm, Map<String, String> errors) {
        if (password != null) {
            if (!password.isBlank()) {
                if (password.length() > 6 && password.length() < 30) {
                    if (!isValidPassword(password, passwordConfirm)) {
                        errors.put("password", "Passwords do not match!");
                    }
                } else {
                    errors.put("password", "Le taille du mot de passe doit être compris entre 6 et 30!");
                }
            } else {
                errors.put("password", "Le mot de passe ne doit pas être vide!");
            }
        } else {
            errors.put("password", "Le mot de passe ne doit pas être nul!");
        }
        return errors;
    }

    public Map<String, String> resetPassword(String email, Map<String, String> errors) {
        User user = getUserByUsernameOrEmail(email);
        if (user == null) {
            errors.put("email", "Cet email n'existe pas!");
            return errors;
        }
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
        try {
            mailSender.send(constructResetTokenEmail(token, user));
        } catch (MessagingException e) {
            errors.put("email", "Une erreur est survenue lors de l'envoi de l'email.");
        }

        return errors;
    }


    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        ptr.save(myToken);
    }

    private MimeMessage constructResetTokenEmail(String token, User user) throws MessagingException {
        String url = "https://divydium.vercel.app/change-password/" + token;
        String message = "Cliquez sur le lien suivant pour modifier votre mot de passe : ";
        return constructEmail("Lien de changement de mot de passe", message + " <a href=\"" + url + "\">Modifier votre mot de passe</a>", user);
    }

    private MimeMessage constructEmail(String subject, String body, User user) throws MessagingException {
        MimeMessage email = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(email, true, "UTF-8");

        String logo = "<img src='https://imagedelivery.net/8Kpv_HmNOrZyvof2hmLoMA/e86b5f76-979a-41ce-a554-53a69b106000/public' alt='Logo' style='width:150px;'>";
        String header = "<h2 style='color: #2C3E50;'>Bienvenue chez Dividium</h2>";
        String footer = "<p style='color: #7F8C8D;'>© 2024 Dividium. Tous droits réservés.</p>";

        String content = "<html><body>" +
                logo +
                header +
                "<p>Bonjour " + user.getPseudo() + ",</p>" +
                "<p>" + body + "</p>" +
                footer +
                "</body></html>";

        helper.setSubject(subject);
        helper.setText(content, true); // True pour indiquer que le contenu est du HTML
        helper.setTo(user.getEmail());
        helper.setFrom(emailapp);

        return email;
    }

    @CachePut(value = "users", key = "#userid.toString()")
    public Map<String, String> savePassword(PasswordDto passwordDTO, Map<String, String> errors) {
        String result = securityService.validatePasswordResetToken(passwordDTO.getToken());

        if(result != null) {
            errors.put("url", "Ce lien n'est pas valide!");
            return errors;
        }

        User user = securityService.getUserByPasswordResetToken(passwordDTO.getToken());
        errors = checkPassword(passwordDTO.getPassword(), passwordDTO.getPasswordConfirmation(), errors);

        if (errors.isEmpty()) {
            changeUserPassword(user, passwordDTO.getPassword());
        }

        return errors;
    }

    @CachePut(value = "users", key = "#user.id.toString()")
    public void changeUserPassword(User user, String password) {
        ptr.deleteById(ptr.findByUser(user).getId());
        user.setMotDePasse(encoder.encode(password));
        save(user);
    }

    @CachePut(value = "users", key = "#user.id.toString()")
    public void EditUser(EditUser user) {
        User userbefore = getUserObjectById(user.getId());
        userbefore.setPseudo(user.getPseudo());
        userbefore.setAvatarUrl(user.getNewavatar());
        userRepository.save(userbefore);
    }

    @Transactional
    public ResponseEntity<ProfileDTO> GetProfileUser(String pseudo, UUID userid) {
        User user = userRepository.findByPseudo(pseudo);
        User userlog = userRepository.findByIdObject(userid);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Récupération des posts sous forme de DTO
        List<RespondPostDTO> posts = postService.getAllPostsbyUser(user.getId());

        // Construction du ProfileDTO
        ProfileDTO dto = new ProfileDTO();
        dto.setUsername(user.getPseudo());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setFollowersCount(user.getFollowers().size());
        dto.setPosts(posts);
        dto.setFollowed(isFollowed(userlog, user.getId())); // je le suis
        dto.setFollowing(isFollowed(user, userlog.getId())); // il me suis

        return ResponseEntity.ok(dto);
    }

    @Transactional
    public ResponseEntity<?> SubProfileUser(String pseudo, UUID userId) {
        User user = userRepository.findByPseudo(pseudo);
        User userlog = userRepository.findByIdObject(userId);
        if (user == userlog) {
            return ResponseEntity.notFound().build();
        }else if (user.getFollowers().contains(userlog)) {
            return ResponseEntity.badRequest().build();
        }else {
            user.addfollowers(userlog);
            userlog.subscribe(user);
            userRepository.save(user);
            userRepository.save(userlog);
            return ResponseEntity.ok().build();
        }
    }

    @Transactional
    public ResponseEntity<?> UnSubProfileUser(String pseudo, UUID userId) {
        User user = userRepository.findByPseudo(pseudo);
        User userlog = userRepository.findByIdObject(userId);
        if (user == userlog) {
            return ResponseEntity.notFound().build();
        }else if (!userlog.getFollowers().contains(user)) {
            user.removefollowers(userlog);
            userlog.unsubscribe(user);
            userRepository.save(user);
            userRepository.save(userlog);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.badRequest().build();
        }
    }

    private Boolean isFollowed(User user, UUID userid) {
        User userlog = userRepository.findByIdObject(userid);
        if (user.getFollowers().contains(userlog)) {
            return true;
        }else  {
            return false;
        }
    }

    public void ChangeroleUser(AdminChangeRoleUser adminChangeRoleUser) {
        User user = userRepository.findByPseudo(adminChangeRoleUser.getPseudo());
        String rawRole = adminChangeRoleUser.getRole()
                .replace("[", "")
                .replace("]", "")
                .replace("ROLE_", "");

        Role role = Role.valueOf(rawRole);
        user.setRole(role);

        userRepository.save(user);
    }

    @Transactional
    public ResponseEntity<List<UserDTO>> GetSubUser(UUID currentUserId) {
        User currentUser = userRepository.findByIdObject(currentUserId);
        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        List<UserDTO> subs = currentUser.getSubscriptions().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(subs);
    }
}
