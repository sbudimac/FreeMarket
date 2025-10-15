package com.freemarket.platform.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

public class Rating {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    private MarketActor rater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id", nullable = false)
    private MarketActor ratedUser;

    @Column(nullable = false)
    private Integer score; // 1-5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Constructors
    public Rating() {}

    public Rating(MarketActor rater, MarketActor ratedUser, Integer score) {
        this.rater = rater;
        this.ratedUser = ratedUser;
        this.score = score;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public MarketActor getRater() { return rater; }
    public void setRater(MarketActor rater) { this.rater = rater; }

    public MarketActor getRatedUser() { return ratedUser; }
    public void setRatedUser(MarketActor ratedUser) { this.ratedUser = ratedUser; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
