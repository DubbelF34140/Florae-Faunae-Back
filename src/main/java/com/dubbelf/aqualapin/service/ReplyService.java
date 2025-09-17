package com.dubbelf.aqualapin.service;

import com.dubbelf.aqualapin.dto.CreateReplyDTO;
import com.dubbelf.aqualapin.dto.ReplyDTO;
import com.dubbelf.aqualapin.entity.Comment;
import com.dubbelf.aqualapin.entity.Post;
import com.dubbelf.aqualapin.entity.User;
import com.dubbelf.aqualapin.repository.CommentRepository;
import com.dubbelf.aqualapin.repository.PostRepository;
import com.dubbelf.aqualapin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReplyDTO createReply(UUID authorId, CreateReplyDTO dto) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment reply = new Comment();
        reply.setContent(dto.getContent());
        reply.setAuthor(author);
        reply.setPost(post);

        if (dto.getParentId() != null) {
            Comment parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            reply.setParent(parent);
        }

        Comment saved = commentRepository.save(reply);

        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ReplyDTO> getRepliesByPost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return commentRepository.findByPost(post).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReply(UUID replyId, UUID currentUserId) {
        Comment reply = commentRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getAuthor().getId().equals(currentUserId)) {
            throw new RuntimeException("Not allowed to delete this reply");
        }

        commentRepository.delete(reply);
    }

    private ReplyDTO toDTO(Comment comment) {
        ReplyDTO dto = new ReplyDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost().getId());
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setAuthorUsername(comment.getAuthor().getPseudo());
        dto.setAuthorAvatar(comment.getAuthor().getAvatarUrl());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
