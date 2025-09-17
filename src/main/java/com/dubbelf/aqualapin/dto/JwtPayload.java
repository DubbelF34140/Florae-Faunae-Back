package com.dubbelf.aqualapin.dto;

import com.dubbelf.aqualapin.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class JwtPayload {
  private String token;

  @Getter
  @Setter
  private UUID id;

  private String username;

  private String email;

  private String role;

  private String avatarUrl;

  @Getter
  @Setter
  private boolean admin;

  public JwtPayload(String accessToken, UUID id, boolean administrateur) {
    this.token = accessToken;
    this.id = id;
    this.admin = administrateur;
  }

    public JwtPayload(String token, UUID id, String username, String email, String role, String avatarUrl, boolean admin) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.admin = admin;
        this.avatarUrl = avatarUrl;
    }

    public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }


    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
