package com.freemarket.platform.repository;

import com.freemarket.platform.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    /**
     * Check whether a rating already exists between a rater and a ratee.
     * <p>
     * Used to enforce "one rating per user pair".
     */
    boolean existsByRater_IdAndRatee_Id(UUID raterId, UUID rateeId);

    /**
     * Retrieve the rating given by a specific rater to a specific ratee.
     * <p>
     * Used for update (PATCH) or delete operations.
     */
    Optional<Rating> findByRater_IdAndRatee_Id(UUID raterId, UUID rateeId);

    /**
     * Delete a rating by rater and ratee.
     * <p>
     * Typically used when allowing rating removal or replacement.
     */
    @Modifying
    void deleteByRater_IdAndRatee_Id(UUID raterId, UUID rateeId);

    /**
     * Retrieve all ratings received by a given user (paged).
     * <p>
     * Used for public profile pages.
     */
    Page<Rating> findAllByRatee_Id(UUID rateeId, Pageable pageable);

    /**
     * Retrieve all ratings authored by a given user (paged).
     * <p>
     * Useful for "my ratings" views or moderation tools.
     */
    Page<Rating> findAllByRater_Id(UUID raterId, Pageable pageable);

    /**
     * Compute the average rating score and total number of ratings
     * received by a given user.
     * <p>
     * Returned as:
     *   [0] -> Double average score
     *   [1] -> Long rating count
     * <p>
     * Prefer wrapping this in a projection for type safety.
     */
    @Query("""
           SELECT AVG(r.score), COUNT(r)
           FROM Rating r
           WHERE r.ratee.id = :rateeId
           """)
    Object[] getSummaryForRatee(@Param("rateeId") UUID rateeId);

    /**
     * Retrieve ratings for a ratee and eagerly load the rater entity.
     * <p>
     * Useful for profile pages where rater info (username, avatar)
     * must be shown without triggering N+1 queries.
     */
    @EntityGraph(attributePaths = {"rater"})
    Page<Rating> findAllWithRaterByRatee_Id(UUID rateeId, Pageable pageable);
}
