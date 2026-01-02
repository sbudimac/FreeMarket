package com.freemarket.platform.repository;

import com.freemarket.platform.entity.Post;
import com.freemarket.platform.entity.PostCategory;
import com.freemarket.platform.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>, JpaSpecificationExecutor<Post> {

    /**
     * Fetch a single post by its ID and eagerly load the owning MarketActor.
     * <p>
     * Use this when you need to access post.getMarketActor() immediately
     * (e.g. post details endpoint showing seller information).
     * <p>
     * This avoids LazyInitializationException and N+1 queries.
     */
    @EntityGraph(attributePaths = {"marketActor"})
    Optional<Post> findWithMarketActorById(UUID id);

    /**
     * Check whether a post exists AND belongs to the given market actor.
     * <p>
     * Typically used for authorization checks:
     * "Is this user allowed to modify/delete this post?"
     */
    boolean existsByIdAndMarketActor_Id(UUID postId, UUID marketActorId);

    /**
     * Fetch a post by ID only if it belongs to the given market actor.
     * <p>
     * Useful for secured update/delete operations where ownership
     * must be enforced at query level.
     */
    Optional<Post> findByIdAndMarketActor_Id(UUID postId, UUID marketActorId);

    /**
     * Retrieve all posts created by a specific market actor (paged).
     * <p>
     * Used for "My Posts" or profile pages.
     */
    Page<Post> findAllByMarketActor_Id(UUID marketActorId, Pageable pageable);

    /**
     * Retrieve all posts of a market actor with a specific status (paged).
     * <p>
     * Example: show only ACTIVE posts of the current user.
     */
    Page<Post> findAllByMarketActor_IdAndStatus(UUID marketActorId, PostStatus status, Pageable pageable);

    /**
     * Retrieve all posts of a market actor matching any of the given statuses.
     * <p>
     * Example: ACTIVE + SOLD posts on a profile page.
     */
    Page<Post> findAllByMarketActor_IdAndStatusIn(UUID marketActorId, Collection<PostStatus> statuses, Pageable pageable);

    /**
     * Retrieve all public posts with the given filtering options.
     */
    Page<Post> findAllByStatus(PostStatus status, Pageable pageable);

    Page<Post> findAllByStatusIn(Collection<PostStatus> statuses, Pageable pageable);

    Page<Post> findAllByStatusAndCategory(PostStatus status, PostCategory category, Pageable pageable);

    Page<Post> findAllByStatusInAndCategoryIn(Collection<PostStatus> statuses, Collection<PostCategory> categories, Pageable pageable);

    /**
     * Perform a simple text search over title and description for public posts.
     * <p>
     * Case-insensitive LIKE-based search.
     */
    @Query("""
           SELECT p
           FROM Post p
           WHERE p.status IN :statuses
             AND (
                   LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%'))
             )
           """)
    Page<Post> searchPublicByQuery(@Param("q") String query,
                                   @Param("statuses") Collection<PostStatus> statuses,
                                   Pageable pageable);

    /**
     * Perform a text search combined with category filtering.
     */
    @Query("""
           SELECT p
           FROM Post p
           WHERE p.status IN :statuses
             AND p.category IN :categories
             AND (
                   LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%'))
             )
           """)
    Page<Post> searchPublicByQueryAndCategories(@Param("q") String query,
                                                @Param("statuses") Collection<PostStatus> statuses,
                                                @Param("categories") Collection<PostCategory> categories,
                                                Pageable pageable);

    /**
     * Retrieve public posts that contain at least one of the given tags.
     * <p>
     * Uses the post_tags element collection.
     */
    @Query("""
           SELECT DISTINCT p
           FROM Post p
           JOIN p.tags t
           WHERE p.status IN :statuses
             AND t IN :tags
           """)
    Page<Post> findPublicByAnyTag(@Param("tags") Collection<String> tags,
                                  @Param("statuses") Collection<PostStatus> statuses,
                                  Pageable pageable);

    /**
     * Retrieve public posts filtered by tags and categories.
     */
    @Query("""
           SELECT DISTINCT p
           FROM Post p
           JOIN p.tags t
           WHERE p.status IN :statuses
             AND p.category IN :categories
             AND t IN :tags
           """)
    Page<Post> findPublicByAnyTagAndCategories(@Param("tags") Collection<String> tags,
                                               @Param("statuses") Collection<PostStatus> statuses,
                                               @Param("categories") Collection<PostCategory> categories,
                                               Pageable pageable);

    /**
     * Atomically increment the view count of a post.
     * <p>
     * Used when a post detail page is viewed.
     * Executed as an UPDATE query to avoid race conditions.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") UUID postId);
}
