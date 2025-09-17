package com.dubbelf.aqualapin.repository;

import com.dubbelf.aqualapin.entity.PasswordResetToken;
import com.dubbelf.aqualapin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);

}