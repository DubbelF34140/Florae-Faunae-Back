package com.dubbelf.aqualapin.repository;

import com.dubbelf.aqualapin.entity.Post;
import com.dubbelf.aqualapin.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID>, JpaSpecificationExecutor<Post> {
    List<Post> findAllByAuthor_Id(UUID id);

    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Post findByIdObject(@Param("id") UUID id);

    List<Post> findAllByAuthorIn(List<User> followedUsers);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
    SELECT DISTINCT c.id
    FROM Post p
    JOIN p.likedBy l
    JOIN p.categories c
    WHERE l.id = :userId
""")
    List<UUID> findAllLikedCategoriesByUser(@Param("userId") UUID userId);


    @Query("""
    SELECT p FROM Post p
    WHERE p.id <> :currentPostId
      AND (p.author.id IN (
            SELECT f.id FROM User u 
            JOIN u.subscriptions f
            WHERE u.id = :userId
          )
          OR EXISTS (
            SELECT 1 FROM p.categories c
            WHERE c.id IN :likedCategoryIds
          ))
    ORDER BY p.createdAt DESC
""")
    List<Post> findNextRecommendedPosts(
            @Param("currentPostId") UUID currentPostId,
            @Param("userId") UUID userId,
            @Param("likedCategoryIds") List<UUID> likedCategoryIds,
            Pageable pageable
    );
}
