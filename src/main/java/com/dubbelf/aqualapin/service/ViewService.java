package com.dubbelf.aqualapin.service;

import com.dubbelf.aqualapin.entity.Post;
import com.dubbelf.aqualapin.repository.PostRepository;
import com.dubbelf.aqualapin.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ViewService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final Map<String, Long> viewCache = new ConcurrentHashMap<>();

    @Transactional
    public void addView(UUID postId, UUID userId, String ip) {
        String key = postId.toString() + ":" + (userId != null ? userId : ip);
        long now = System.currentTimeMillis();

        if (!viewCache.containsKey(key) || now - viewCache.get(key) > 900_000) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            post.setViews(post.getViews() + 1);
            postRepository.save(post);

            viewCache.put(key, now);
        }
    }
}
