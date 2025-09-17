package com.dubbelf.aqualapin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    private String color = "#000000";

    private String icon;

    @Column(unique = true, nullable = false)
    private String slug;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Post> posts;

}
