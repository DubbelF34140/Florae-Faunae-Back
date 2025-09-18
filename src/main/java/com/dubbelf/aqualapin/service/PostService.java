package com.dubbelf.aqualapin.service;

import com.dubbelf.aqualapin.converter.RespondPostConverter;
import com.dubbelf.aqualapin.dto.ForumStatsDTO;
import com.dubbelf.aqualapin.dto.PostDTO;
import com.dubbelf.aqualapin.dto.RespondPostDTO;
import com.dubbelf.aqualapin.entity.*;
import com.dubbelf.aqualapin.repository.CategoryRepository;
import com.dubbelf.aqualapin.repository.CommentRepository;
import com.dubbelf.aqualapin.repository.PostRepository;
import com.dubbelf.aqualapin.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public PostDTO createPost(UUID authorId, List<UUID> categoryIds, String title, String content) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.isEmpty()) {
            throw new RuntimeException("No valid categories found");
        }

        Post post = new Post();
        post.setTitle(title);
        System.out.print(content);
        post.setContent(content);
        post.setAuthor(author);
        post.setCategories(categories);
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        post.setViews(0);

        Post savedPost = postRepository.save(post);

        // Mapper vers DTO simplifié
        PostDTO result = new PostDTO();
        result.setAuthorId(savedPost.getAuthor().getId());
        result.setCategories(
                savedPost.getCategories().stream()
                        .map(Category::getId)
                        .toList()
        );
        result.setTitle(savedPost.getTitle());
        result.setContent(savedPost.getContent());

        return result;
    }



    public void updatePost(
            UUID userId,
            UUID postId,
            String title,
            String content,
            List<UUID> categoryIds) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.isEmpty()) {
            throw new RuntimeException("No valid categories found");
        }

        boolean canEdit = user.getId().equals(post.getAuthor().getId())
                || user.getRole() == Role.MODERATOR
                || user.getRole() == Role.ADMIN;

        if (canEdit) {
            post.setTitle(title);
            post.setContent(content);
            post.setUpdatedAt(Instant.now());
            post.setCategories(categories);
            postRepository.save(post);
        }
    }


    public void deletePost(UUID postId, UUID userid) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userid).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getId().equals(post.getAuthor().getId())) {
            postRepository.deleteById(postId);
        } else if (user.getRole() == Role.MODERATOR || user.getRole() == Role.ADMIN) {
            postRepository.deleteById(postId);
        }
    }

    @Transactional
    public List<RespondPostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(RespondPostConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<RespondPostDTO> getMyAllPosts(UUID userId) {
        List<Post> posts = postRepository.findAllByAuthor_Id(userId);
        return posts.stream()
                .map(RespondPostConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<RespondPostDTO> getAllPostsbyUser(UUID userId) {
        List<Post> posts = postRepository.findAllByAuthor_Id(userId);
        return posts.stream()
                .map(RespondPostConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RespondPostDTO getPost(UUID id, UUID userId) {
        Post post =  postRepository.findByIdObject(id);
        User user = userRepository.findByIdObject(userId);
        return RespondPostConverter.toDTOlike(post, user);
    }

    @Transactional
    public RespondPostDTO getPostNoLogin(UUID id) {
        Post post =  postRepository.findByIdObject(id);
        return RespondPostConverter.toDTO(post);
    }

    @Transactional
    public List<RespondPostDTO> getSubscribedPosts(UUID userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Récupérer tous les users suivis par currentUser
        List<User> followedUsers = userRepository.findAll().stream()
                .filter(u -> u.getFollowers().contains(currentUser))
                .collect(Collectors.toList());

        if (followedUsers.isEmpty()) {
            return List.of();
        }

        // Récupérer tous les posts de ces users
        List<Post> posts = postRepository.findAllByAuthorIn(followedUsers);

        // Transformer en DTO
        return posts.stream()
                .map(RespondPostConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleLike(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (post.isLikedBy(user)) {
            post.removeLike(user);
        } else {
            post.addLike(user);
        }

        postRepository.save(post);
    }


    @Transactional
    public List<RespondPostDTO> getRecentPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .stream()
                .map(RespondPostConverter::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional()
    public ForumStatsDTO getForumStats() {
        long members = userRepository.count();
        long subjects = postRepository.count();
        long messages = commentRepository.count();

        return new ForumStatsDTO(members, messages, subjects);
    }

    @Transactional
    public List<RespondPostDTO> searchPosts(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String[] keywords = query.toLowerCase().split("\\s+");

        Specification<Post> spec = null;
        for (String keyword : keywords) {
            Specification<Post> keywordSpec = PostSpecifications.containsTextInFields(keyword);
            spec = (spec == null) ? keywordSpec : spec.and(keywordSpec);
        }

        List<Post> results = postRepository.findAll(spec);
        return results.stream()
                .map(RespondPostConverter::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public RespondPostDTO getNextShort(UUID currentPostId, UUID userId) {
        Post currentPost = postRepository.findById(currentPostId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Récupérer les catégories likées par l’utilisateur
        List<UUID> likedCategoryIds = postRepository.findAllLikedCategoriesByUser(user.getId());

        // 2. Chercher un post recommandé (un seul résultat max)
        List<Post> nextPosts = postRepository.findNextRecommendedPosts(
                currentPost.getId(),
                user.getId(),
                likedCategoryIds,
                PageRequest.of(0, 1)
        );

        if (nextPosts.isEmpty()) {
            return null;
        }

        return RespondPostConverter.toDTOlike(nextPosts.get(0), user);
    }

    @Transactional
    public RespondPostDTO getFirstShort(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Récupérer les catégories likées par l’utilisateur
        List<UUID> likedCategoryIds = postRepository.findAllLikedCategoriesByUser(user.getId());

        // 2. Chercher un post recommandé sans exclure de currentPost
        List<Post> nextPosts = postRepository.findNextRecommendedPosts(
                UUID.randomUUID(), // valeur bidon qui ne matche rien
                user.getId(),
                likedCategoryIds,
                PageRequest.of(0, 1)
        );

        Post firstPost;

        if (!nextPosts.isEmpty()) {
            firstPost = nextPosts.get(0);
        } else {
            // fallback : dernier post créé dans tout le forum
            firstPost = postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No posts available"));
        }

        return RespondPostConverter.toDTOlike(firstPost, user);
    }

}
