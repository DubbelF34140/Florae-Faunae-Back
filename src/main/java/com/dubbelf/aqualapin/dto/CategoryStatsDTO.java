package com.dubbelf.aqualapin.dto;

public class CategoryStatsDTO {
    private String name;
    private String description;
    private String color;
    private String icon;
    private long postCount;
    private long commentCount;

    public CategoryStatsDTO(String name, String description, String color, String icon, long postCount, long commentCount) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
        this.postCount = postCount;
        this.commentCount = commentCount;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getPostCount() {
        return postCount;
    }

    public long getCommentCount() {
        return commentCount;
    }
}
