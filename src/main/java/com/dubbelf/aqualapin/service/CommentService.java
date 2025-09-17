package com.dubbelf.aqualapin.service;

import com.dubbelf.aqualapin.entity.Comment;
import com.dubbelf.aqualapin.entity.Post;
import com.dubbelf.aqualapin.entity.User;
import com.dubbelf.aqualapin.repository.CommentRepository;
import com.dubbelf.aqualapin.repository.PostRepository;
import com.dubbelf.aqualapin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Comment createComment(UUID postId, UUID authorId, String content, UUID parentId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        User author = userRepository.findById(authorId).orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setContent(content);

        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId).orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParent(parent);
        }

        return commentRepository.save(comment);
    }

    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }
}
