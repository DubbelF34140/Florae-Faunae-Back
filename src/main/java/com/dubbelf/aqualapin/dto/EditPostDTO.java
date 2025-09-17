package com.dubbelf.aqualapin.dto;

import java.util.List;
import java.util.UUID;

public class EditPostDTO {

    private String title;

    private String content;

    private List<UUID> categories;

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
