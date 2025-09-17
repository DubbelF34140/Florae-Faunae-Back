package com.dubbelf.aqualapin.dto;

import java.util.List;

public class ProfileDTO {

    private String pseudo;
    private String avatarUrl;
    private int followersCount;
    private List<RespondPostDTO> posts;
    private Boolean following;
    private Boolean followed;

    public ProfileDTO() {
    }

    public ProfileDTO(String username, String avatarUrl, int followersCount, List<RespondPostDTO> posts,  Boolean following, Boolean followed) {
        this.pseudo = pseudo;
        this.avatarUrl = avatarUrl;
        this.followersCount = followersCount;
        this.posts = posts;
        this.following = following;
        this.followed = followed;
    }

    public String getUsername() {
        return pseudo;
    }

    public void setUsername(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public List<RespondPostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<RespondPostDTO> posts) {
        this.posts = posts;
    }

    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}
