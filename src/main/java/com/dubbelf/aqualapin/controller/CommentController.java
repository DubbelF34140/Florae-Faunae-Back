package com.dubbelf.aqualapin.controller;

import com.dubbelf.aqualapin.entity.Comment;
import com.dubbelf.aqualapin.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestParam UUID postId,
                                                 @RequestParam UUID authorId,
                                                 @RequestParam String content,
                                                 @RequestParam(required = false) UUID parentId) {
        return ResponseEntity.ok(commentService.createComment(postId, authorId, content, parentId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
