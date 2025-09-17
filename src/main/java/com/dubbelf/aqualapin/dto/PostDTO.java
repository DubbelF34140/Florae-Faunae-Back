package com.dubbelf.aqualapin.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class PostDTO {

    private UUID authorId;

    private String title;

    private String content; // HTML éditable via frontend

    private List<UUID> categories;

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<UUID> getCategories() {
        return categories;
    }

    public void setCategories(List<UUID> categories) {
        this.categories = categories;
    }
}
