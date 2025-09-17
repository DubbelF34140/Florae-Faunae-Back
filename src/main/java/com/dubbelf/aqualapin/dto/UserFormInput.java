package com.dubbelf.aqualapin.dto;

import com.dubbelf.aqualapin.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class UserFormInput {
    @Getter
    @Setter
    @NotNull
    @NotBlank
    @Size(min = 3, max = 30)
    private String pseudo;

    @Getter
    @Setter
    @NotNull
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Getter
    @Setter
    private String oldPassword;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String passwordConfirmation;

    public User toUtilisateur() {
        return new User(
                this.pseudo,
                this.email,
                this.password
        );
    }

}