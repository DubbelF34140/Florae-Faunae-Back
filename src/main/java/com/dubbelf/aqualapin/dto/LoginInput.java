package com.dubbelf.aqualapin.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginInput {
    @NotBlank
    private String pseudo;

    @NotBlank
    private String password;

    public String getpseudo() {
        return pseudo;
    }

    public void setpseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}