package com.dubbelf.aqualapin.service;

import com.dubbelf.aqualapin.entity.PasswordResetToken;
import com.dubbelf.aqualapin.entity.User;
import com.dubbelf.aqualapin.repository.PasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Override
    public User getUserByPasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return passToken.getUser();
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        return passToken.getExpiryDate().isBefore(LocalDateTime.now());
    }
}
