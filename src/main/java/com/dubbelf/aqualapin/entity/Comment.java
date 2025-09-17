package com.dubbelf.aqualapin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    private User author;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    private Comment parent;

    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies;
}
