package com.freemarket.platform.service;

import com.freemarket.platform.dto.PostDetailsDto;
import com.freemarket.platform.dto.PostListItemDto;
import com.freemarket.platform.dto.UserPublicDto;
import com.freemarket.platform.dto.request.PostCreateRequest;
import com.freemarket.platform.dto.request.PostPatchRequest;
import com.freemarket.platform.entity.MarketActor;
import com.freemarket.platform.entity.Post;
import com.freemarket.platform.entity.PostCategory;
import com.freemarket.platform.entity.PostStatus;
import com.freemarket.platform.exception.ForbiddenException;
import com.freemarket.platform.exception.NotFoundException;
import com.freemarket.platform.repository.MarketActorRepository;
import com.freemarket.platform.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final MarketActorRepository marketActorRepository;

    @Autowired
    public PostService(PostRepository postRepository, MarketActorRepository marketActorRepository) {
        this.postRepository = postRepository;
        this.marketActorRepository = marketActorRepository;
    }

    @Transactional
    public UUID createPost(String username, PostCreateRequest request) {
        MarketActor marketActor = marketActorRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("MarketActor with username: " + username + "not found"));

        Post post = new Post();
        post.setMarketActor(marketActor);
        post.setCategory(request.category());
        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setLocation(request.location());
        post.setPriceInfo(request.priceInfo());
        post.setContactInfo(request.contactInfo());

        if (StringUtils.hasText(request.currency())) {
            post.setCurrency(request.currency());
        }

        post.setCondition(request.condition());
        post.setStatus(PostStatus.ACTIVE);
        post.setViewCount(0L);

        if (request.tags() != null) {
            post.setTags(new HashSet<>(request.tags()));
        }

        if (StringUtils.hasText(request.thumbnailUrl())) {
            post.setImages(new ArrayList<>(request.images()));
        } else if (request.images() != null && !request.images().isEmpty()) {
            post.setThumbnailUrl(request.images().get(0));
        }

        return postRepository.save(post).getId();
    }

    @Transactional
    public PostDetailsDto getPost(UUID postId, boolean incrementView) {
        Post post = postRepository.findWithDetailsById(postId)
                .orElseThrow(() -> new NotFoundException("Post with id: " + postId + "not found"));

        if (incrementView) {
            postRepository.incrementViewCount(postId);
            post.setViewCount(post.getViewCount() + 1);
        }

        return toPostDetailsDto(post);
    }

    @Transactional
    public Page<PostListItemDto> listPosts(
            String query,
            List<String> tags,
            List<String> categories,
            List<String> statuses,
            Pageable pageable
    ) {
        Collection<PostStatus> sts = parseStatuses(statuses);
        Collection<PostCategory> cats = parseCategories(categories);

        boolean hasQuery = StringUtils.hasText(query);
        boolean hasTags = tags != null && !tags.isEmpty();
        boolean hasCategories = categories != null && !categories.isEmpty();

        Page<Post> page;

        if (hasTags && hasCategories) {
            page = postRepository.findPublicByAnyTagAndCategories(tags, sts, cats, pageable);
        } else if (hasTags) {
            page = postRepository.findPublicByAnyTag(tags, sts, pageable);
        } else if (hasQuery && hasCategories) {
            page = postRepository.searchPublicByQueryAndCategories(query, sts, cats, pageable);
        } else if (hasQuery) {
            page = postRepository.searchPublicByQuery(query, sts, pageable);
        } else if (hasCategories) {
            page = postRepository.findAllByStatusInAndCategoryIn(sts, cats, pageable);
        } else {
            page = postRepository.findAllByStatusIn(sts, pageable);
        }

        return page.map(this::toPostListItemDto);
    }

    @Transactional
    public PostDetailsDto patchPost(UUID marketActorId, UUID postId, PostPatchRequest request) {
        Post post = postRepository.findWithDetailsById(postId)
                .orElseThrow(() -> new NotFoundException("Post with id: " + postId + "not found"));

        if (!post.getMarketActor().getId().equals(marketActorId)) {
            throw new ForbiddenException("You do not own this post");
        }

        if (request.category() != null) post.setCategory(request.category());
        if (request.title() != null) post.setTitle(request.title());
        if (request.description() != null) post.setDescription(request.description());
        if (request.location() != null) post.setLocation(request.location());
        if (request.priceInfo() != null) post.setPriceInfo(request.priceInfo());
        if (request.contactInfo() != null) post.setContactInfo(request.contactInfo());
        if (request.currency() != null) post.setCurrency(request.currency());
        if (request.condition() != null) post.setCondition(request.condition());
        if (request.status() != null) post.setStatus(request.status());

        if (request.tags() != null) post.setTags(new HashSet<>(request.tags()));
        if (request.images() != null) post.setImages(new ArrayList<>(request.images()));
        if (request.thumbnailUrl() != null) {
            post.setThumbnailUrl(request.thumbnailUrl());
        } else if (request.images() != null && !request.images().isEmpty()) {
            post.setThumbnailUrl(request.images().get(0));
        }

        return toPostDetailsDto(post);
    }

    @Transactional
    public void deletePost(UUID marketActorId, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with id: " + postId + "not found"));

        if (!postRepository.existsByIdAndMarketActor_Id(postId, marketActorId)) {
            throw new ForbiddenException("You do not own this post");
        }

        postRepository.delete(post);
    }

    @Transactional
    public Page<PostListItemDto> listMyPosts(UUID marketActorId, PostStatus status, Pageable pageable) {
        Page<Post> page = (status == null)
                ? postRepository.findAllByMarketActor_Id(marketActorId, pageable)
                : postRepository.findAllByMarketActor_IdAndStatus(marketActorId, status, pageable);

        return page.map(this::toPostListItemDto);
    }

    private PostListItemDto toPostListItemDto(Post p) {
        UserPublicDto owner = new UserPublicDto(
                p.getMarketActor().getId(),
                p.getMarketActor().getUsername()
        );

        return new PostListItemDto(
                p.getId(),
                p.getTitle(),
                p.getCategory(),
                p.getStatus(),
                p.getLocation(),
                p.getPriceInfo(),
                p.getCurrency(),
                p.getThumbnailUrl(),
                p.getCreatedAt(),
                owner
        );
    }

    private PostDetailsDto toPostDetailsDto(Post p) {
        UserPublicDto owner = new UserPublicDto(
                p.getMarketActor().getId(),
                p.getMarketActor().getUsername()
        );

        return new PostDetailsDto(
                p.getId(),
                p.getCategory(),
                p.getTitle(),
                p.getDescription(),
                p.getLocation(),
                p.getPriceInfo(),
                p.getCurrency(),
                p.getCondition(),
                p.getStatus(),
                p.getThumbnailUrl(),
                p.getImages() == null ? List.of() : List.copyOf(p.getImages()),
                p.getTags() == null ? Set.of() : Set.copyOf(p.getTags()),
                p.getViewCount() == null ? 0 : p.getViewCount(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                owner
        );
    }

    private Collection<PostStatus> parseStatuses(List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return List.of(PostStatus.ACTIVE);
        }
        List<PostStatus> statuses = new ArrayList<>();
        for (String s : raw) {
            statuses.add(PostStatus.valueOf(s));
        }
        return statuses;
    }

    private Collection<PostCategory> parseCategories(List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return List.of();
        }
        List<PostCategory> categories = new ArrayList<>();
        for (String s : raw) {
            categories.add(PostCategory.valueOf(s));
        }
        return categories;
    }
}
