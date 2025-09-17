package com.dubbelf.aqualapin.dto;

public class ForumStatsDTO {
    private long members;
    private long messages;
    private long subjects;

    public ForumStatsDTO(long members, long messages, long subjects) {
        this.members = members;
        this.messages = messages;
        this.subjects = subjects;
    }

    public long getMembers() {
        return members;
    }

    public long getMessages() {
        return messages;
    }

    public long getSubjects() {
        return subjects;
    }
}
