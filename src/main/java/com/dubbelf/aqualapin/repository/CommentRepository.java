package com.dubbelf.aqualapin.repository;

import com.dubbelf.aqualapin.entity.Comment;
import com.dubbelf.aqualapin.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByPost(Post post);
    List<Comment> findByParent(Comment parent);

}
