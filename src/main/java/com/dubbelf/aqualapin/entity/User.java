package com.dubbelf.aqualapin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String pseudo;

    @Column(unique = true, nullable = false)
    private String email;

    @NotNull
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private String avatarUrl;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToMany
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "subscribed_to_id")
    )
    private Set<User> subscriptions = new HashSet<>();

    @ManyToMany(mappedBy = "subscriptions")
    private Set<User> followers = new HashSet<>();

    @ManyToMany(mappedBy = "likedBy")
    private List<Post> likedPosts = new ArrayList<>();


    public User(UUID id, String pseudo, String email, String motDePasse, Role role, String avatarUrl, Instant createdAt) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
    }

    public User() {

    }

    public User(@NotNull @NotBlank @Size(min = 3, max = 30) String pseudo, @NotNull @NotBlank @Size(max = 50) @Email String email, String password) {
        this.pseudo = pseudo;
        this.email = email;
        this.motDePasse = password;
        this.role = Role.USER;
        this.createdAt = Instant.now();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<User> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(Set<User> subscriptions) { this.subscriptions = subscriptions; }

    public Set<User> getFollowers() { return followers; }
    public void setFollowers(Set<User> followers) { this.followers = followers; }

    // --- Helpers ---
    public void subscribe(User user) {
        subscriptions.add(user);
        user.getFollowers().add(this);
    }

    public void unsubscribe(User user) {
        subscriptions.remove(user);
        user.getFollowers().remove(this);
    }

    public void addfollowers(User user) {
        followers.add(user);
        user.getFollowers().add(this);
    }

    public void removefollowers(User user) {
        followers.remove(user);
        user.getFollowers().remove(this);
    }

    public List<Post> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(List<Post> likedPosts) {
        this.likedPosts = likedPosts;
    }
}
