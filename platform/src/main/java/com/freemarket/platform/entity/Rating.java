package com.freemarket.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rating", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"rater_id", "rated_market_actor_id"})
})
public class Rating {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    private MarketActor rater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_market_actor_id", nullable = false) // CHANGED: rated_user_id → rated_market_actor_id
    private MarketActor ratedMarketActor; // CHANGED: ratedUser → ratedMarketActor

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer score; // 1-5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Constructors
    public Rating() {}

    public Rating(MarketActor rater, MarketActor ratedMarketActor, Integer score) { // CHANGED
        this.rater = rater;
        this.ratedMarketActor = ratedMarketActor;
        this.score = score;
    }

    public Rating(MarketActor rater, MarketActor ratedMarketActor, Integer score, String comment) { // CHANGED
        this.rater = rater;
        this.ratedMarketActor = ratedMarketActor;
        this.score = score;
        this.comment = comment;
    }

    // Business methods
    public boolean isPerfectScore() {
        return score != null && score == 5;
    }

    public boolean isPoorScore() {
        return score != null && score <= 2;
    }

    public void updateComment(String newComment) {
        this.comment = newComment;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public MarketActor getRater() { return rater; }
    public void setRater(MarketActor rater) { this.rater = rater; }

    public MarketActor getRatedMarketActor() { return ratedMarketActor; } // CHANGED
    public void setRatedMarketActor(MarketActor ratedMarketActor) { this.ratedMarketActor = ratedMarketActor; } // CHANGED

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", rater=" + (rater != null ? rater.getId() : "null") +
                ", ratedMarketActor=" + (ratedMarketActor != null ? ratedMarketActor.getId() : "null") +
                ", score=" + score +
                '}';
    }
}