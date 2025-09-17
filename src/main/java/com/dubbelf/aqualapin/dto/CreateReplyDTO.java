package com.dubbelf.aqualapin.dto;

import java.util.UUID;

public class CreateReplyDTO {
    private UUID postId;
    private UUID parentId; // peut être null si c'est un commentaire direct
    private String content;

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
