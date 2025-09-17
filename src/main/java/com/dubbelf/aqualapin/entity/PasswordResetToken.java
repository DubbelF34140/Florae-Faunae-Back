package com.dubbelf.aqualapin.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Data
@Entity
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION);

    public PasswordResetToken() {}

    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
