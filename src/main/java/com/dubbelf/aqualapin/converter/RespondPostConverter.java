package com.dubbelf.aqualapin.converter;

import com.dubbelf.aqualapin.dto.RespondCategoryDTO;
import com.dubbelf.aqualapin.dto.RespondPostDTO;
import com.dubbelf.aqualapin.entity.Category;
import com.dubbelf.aqualapin.entity.Post;
import com.dubbelf.aqualapin.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class RespondPostConverter {

    private static List<RespondCategoryDTO> mapCategories(List<Category> categories) {
        if (categories == null) return List.of();
        return categories.stream()
                .map(cat -> new RespondCategoryDTO(
                        cat.getId(),
                        cat.getColor(),
                        cat.getName(),
                        cat.getIcon()
                ))
                .collect(Collectors.toList());
    }

    public static RespondPostDTO toDTO(Post post) {
        return new RespondPostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                mapCategories(post.getCategories()),
                post.getAuthor().getPseudo(),
                post.getAuthor().getAvatarUrl(),
                post.getViews(),
                post.getLikes(),
                null,
                post.getComments() != null ? post.getComments().size() : 0
        );
    }

    public static RespondPostDTO toDTOlike(Post post, User user) {
        return new RespondPostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                mapCategories(post.getCategories()),
                post.getAuthor().getPseudo(),
                post.getAuthor().getAvatarUrl(),
                post.getViews(),
                post.getLikes(),
                post.getLikedBy().contains(user),
                post.getComments() != null ? post.getComments().size() : 0
        );
    }
}
