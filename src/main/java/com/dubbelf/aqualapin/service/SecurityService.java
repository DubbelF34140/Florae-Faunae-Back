package com.dubbelf.aqualapin.service;


import com.dubbelf.aqualapin.entity.User;

public interface SecurityService {
    String validatePasswordResetToken(String token);

    User getUserByPasswordResetToken(String token);
}
