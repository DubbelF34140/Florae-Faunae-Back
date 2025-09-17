package com.dubbelf.aqualapin.repository;

import com.dubbelf.aqualapin.entity.Post;
import com.dubbelf.aqualapin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByAuthor_Id(UUID id);

    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Post findByIdObject(@Param("id") UUID id);

    List<Post> findAllByAuthorIn(List<User> followedUsers);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
