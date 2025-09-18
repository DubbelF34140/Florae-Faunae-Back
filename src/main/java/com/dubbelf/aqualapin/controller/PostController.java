package com.dubbelf.aqualapin.controller;

import com.dubbelf.aqualapin.config.JwtUtils;
import com.dubbelf.aqualapin.converter.RespondPostConverter;
import com.dubbelf.aqualapin.dto.*;
import com.dubbelf.aqualapin.entity.Post;
import com.dubbelf.aqualapin.service.PostService;
import com.dubbelf.aqualapin.service.ReplyService;
import com.dubbelf.aqualapin.service.ViewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.dubbelf.aqualapin.config.JwtUtils.parseJwt;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final JwtUtils jwtUtils;
    private final PostService postService;
    private final ViewService viewService;
    private final ReplyService replyService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostDTO postDTO) {
        if (postDTO.getAuthorId() == null || postDTO.getCategories() == null) {
            return ResponseEntity.badRequest().build();
        }

        PostDTO post = postService.createPost(
                postDTO.getAuthorId(),
                postDTO.getCategories(),
                postDTO.getTitle(),
                postDTO.getContent()
        );

        return ResponseEntity.ok(post);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<RespondPostDTO>> getRecentPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.getRecentPosts(page, size));
    }


    @GetMapping("/stats")
    public ResponseEntity<ForumStatsDTO> getForumStats() {
        return ResponseEntity.ok(postService.getForumStats());
    }

    @PostMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @RequestBody EditPostDTO editPostDTO,
            @PathVariable UUID postId,
            HttpServletRequest httpRequest) {

        String jwtToken = parseJwt(httpRequest);
        if (jwtToken != null) {
            UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
            postService.updatePost(currentUserId, postId, editPostDTO.getTitle(), editPostDTO.getContent(), editPostDTO.getCategories());
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping
    public ResponseEntity<List<RespondPostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyAllPosts(HttpServletRequest httpRequest) {
        String jwtToken = parseJwt(httpRequest);

        if (jwtToken != null) {
            UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
            List<RespondPostDTO> Userpost = postService.getMyAllPosts(currentUserId);
            return ResponseEntity.ok(Userpost);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{postId:[0-9a-fA-F\\-]{36}}")
    public ResponseEntity<RespondPostDTO> getPost(@PathVariable UUID postId, HttpServletRequest request) {
        String jwtToken = JwtUtils.parseJwt(request);
        UUID userId = jwtToken != null ? jwtUtils.getIDFromJwtToken(jwtToken) : null;
        return ResponseEntity.ok(postService.getPost(postId, userId));
    }

    @GetMapping("/logout/{postId:[0-9a-fA-F\\-]{36}}")
    public ResponseEntity<RespondPostDTO> getPostNoLogin(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPostNoLogin(postId));
    }

    @PostMapping("/{postId}/view")
    public ResponseEntity<?> addView(@PathVariable UUID postId, HttpServletRequest request) {
        String jwtToken = JwtUtils.parseJwt(request);
        UUID userId = jwtToken != null ? jwtUtils.getIDFromJwtToken(jwtToken) : null;
        String ip = request.getRemoteAddr();

        viewService.addView(postId, userId, ip);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable UUID postId, HttpServletRequest request) {
        String jwtToken = parseJwt(request);
        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = jwtUtils.getIDFromJwtToken(jwtToken);
        postService.toggleLike(postId, userId);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId, HttpServletRequest httpRequest) {
        String jwtToken = parseJwt(httpRequest);
        if (jwtToken != null) {
            UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
            postService.deletePost(postId, currentUserId);
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<?> getSubscribedPosts(HttpServletRequest httpRequest) {
        String jwtToken = parseJwt(httpRequest);

        if (jwtToken != null) {
            UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
            List<RespondPostDTO> subscribedPosts = postService.getSubscribedPosts(currentUserId);
            return ResponseEntity.ok(subscribedPosts);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @PostMapping("/{postId}/replies")
    public ResponseEntity<ReplyDTO> createReply(
            @PathVariable UUID postId,
            @RequestBody CreateReplyDTO dto,
            HttpServletRequest httpRequest
    ) {
        String jwtToken = parseJwt(httpRequest);
        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
        dto.setPostId(postId);
        ReplyDTO reply = replyService.createReply(currentUserId, dto);
        return ResponseEntity.ok(reply);
    }

    @GetMapping("/{postId}/replies")
    public ResponseEntity<List<ReplyDTO>> getReplies(@PathVariable UUID postId) {
        return ResponseEntity.ok(replyService.getRepliesByPost(postId));
    }

    @DeleteMapping("/{postId}/replies/{replyId}")
    public ResponseEntity<?> deleteReply(
            @PathVariable UUID replyId,
            @PathVariable UUID postId,
            HttpServletRequest httpRequest
    ) {
        String jwtToken = parseJwt(httpRequest);
        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
        replyService.deleteReply(replyId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RespondPostDTO>> searchPosts(@RequestParam String query) {
        List<RespondPostDTO> results = postService.searchPosts(query);
        return ResponseEntity.ok(results);
    }


    @GetMapping("/short/{postId}")
    public ResponseEntity<RespondPostDTO> getNextShort(
            @PathVariable UUID postId,
            HttpServletRequest httpRequest
    ) {
        String jwtToken = parseJwt(httpRequest);
        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = jwtUtils.getIDFromJwtToken(jwtToken);

        RespondPostDTO nextPost = postService.getNextShort(postId, userId);
        if (nextPost == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(nextPost);
    }


    @GetMapping("/short")
    public ResponseEntity<RespondPostDTO> getFirstShort(
            HttpServletRequest httpRequest
    ) {
        String jwtToken = parseJwt(httpRequest);
        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = jwtUtils.getIDFromJwtToken(jwtToken);

        RespondPostDTO nextPost = postService.getFirstShort( userId);
        if (nextPost == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(nextPost);
    }


}
