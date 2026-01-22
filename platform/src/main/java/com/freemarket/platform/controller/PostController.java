package com.freemarket.platform.controller;

import com.freemarket.platform.dto.PostDetailsDto;
import com.freemarket.platform.dto.PostListItemDto;
import com.freemarket.platform.dto.request.PostCreateRequest;
import com.freemarket.platform.dto.request.PostPatchRequest;
import com.freemarket.platform.entity.PostStatus;
import com.freemarket.platform.security.CurrentUserRetreiver;
import com.freemarket.platform.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public IdResponse create(@Valid @RequestBody PostCreateRequest request, Authentication auth) {
        String username = auth.getName();
        UUID id = postService.createPost(username, request);
        return new IdResponse(id);
    }

    @GetMapping("/{postId}")
    public PostDetailsDto getFullPost(@PathVariable UUID postId,
                                      @RequestParam(defaultValue = "true") boolean incrementView) {
        return postService.getPost(postId, incrementView);
    }

    @GetMapping
    public Page<PostListItemDto> getFilteredPosts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> tag,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) List<String> status,
            Pageable pageable
    ) {
        return postService.listPosts(query, tag, category, status, pageable);
    }

    @PatchMapping("/{postId}")
    public PostDetailsDto updatePost(@PathVariable UUID postId,
                                     @Valid @RequestBody PostPatchRequest request,
                                     Authentication auth) {
        UUID marketActorId = CurrentUserRetreiver.getCurrentUserID(auth);
        return postService.patchPost(marketActorId, postId, request);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable UUID postId, Authentication auth) {
        UUID marketActorId = CurrentUserRetreiver.getCurrentUserID(auth);
        postService.deletePost(marketActorId, postId);
    }

    @GetMapping("/me")
    public Page<PostListItemDto> getMyPosts(@RequestParam(required = false) PostStatus status,
                                            Pageable pageable,
                                            Authentication auth) {
        UUID marketActorId = CurrentUserRetreiver.getCurrentUserID(auth);
        return postService.listMyPosts(marketActorId, status, pageable);
    }

    public record IdResponse(UUID id) {}
}
