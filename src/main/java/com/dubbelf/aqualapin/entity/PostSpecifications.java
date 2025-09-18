package com.dubbelf.aqualapin.entity;

import org.springframework.data.jpa.domain.Specification;

public class PostSpecifications {
    public static Specification<Post> containsTextInFields(String keyword) {
        return (root, query, builder) -> {
            String likePattern = "%" + keyword.toLowerCase() + "%";

            return builder.or(
                    builder.like(builder.lower(root.get("title")), likePattern),
                    builder.like(builder.lower(root.join("author").get("pseudo")), likePattern),
                    builder.like(builder.lower(root.join("categories").get("name")), likePattern)
            );
        };
    }

}
