package com.dubbelf.aqualapin.dto;

import com.dubbelf.aqualapin.entity.Role;

import java.util.UUID;

public class EditUser {

    private UUID id;
    private String pseudo;
    private String newAvatarUrl;

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

    public String getNewavatar() {
        return newAvatarUrl;
    }

    public void setNewavatar(String newAvatarUrl) {
        this.newAvatarUrl = newAvatarUrl;
    }
}
