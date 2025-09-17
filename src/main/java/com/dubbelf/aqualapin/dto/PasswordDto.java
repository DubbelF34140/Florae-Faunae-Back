package com.dubbelf.aqualapin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordDto {

    @NotNull
    private String password;

    @NotNull
    private String passwordConfirmation;

    @NotNull
    private  String token;

}