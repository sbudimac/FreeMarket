package com.freemarket.platform.repository;
import com.freemarket.platform.entity.MarketActor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarketActorRepository extends JpaRepository<MarketActor, UUID> {
    // Find by username
    Optional<MarketActor> findByUsername(String username);

    // Find by email
    Optional<MarketActor> findByEmail(String email);

    // Check if username exists
    boolean existsByUsername(String username);

    // Check if email exists
    boolean existsByEmail(String email);

    // Find all verified users
    List<MarketActor> findByIsVerifiedTrue();

    // Find all unverified users
    List<MarketActor> findByIsVerifiedFalse();

    // Search by username containing (for search functionality)
    List<MarketActor> findByUsernameContainingIgnoreCase(String username);

    // Custom query to find users with active posts
    @Query("SELECT DISTINCT ma FROM MarketActor ma JOIN ma.posts p WHERE p.isActive = true")
    List<MarketActor> findWithActivePosts();

    // Custom query to find users by location pattern in their posts
    @Query("SELECT DISTINCT ma FROM MarketActor ma JOIN ma.posts p WHERE p.location LIKE %:location%")
    List<MarketActor> findByPostLocationContaining(@Param("location") String location);

    // Count users by verification status
    long countByIsVerified(boolean isVerified);
}
