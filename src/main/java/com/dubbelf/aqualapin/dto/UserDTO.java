package com.dubbelf.aqualapin.dto;

import com.dubbelf.aqualapin.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String pseudo;
    private String role;
    private String email;
    private String avatarUrl;

    public UserDTO(User user) {
        this.id = user.getId();
        this.role = user.getRole().toString();
        this.pseudo = user.getPseudo();
        this.email = user.getEmail();
        this.avatarUrl = user.getAvatarUrl();
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
