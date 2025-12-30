package com.freemarket.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
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
    @JoinColumn(name = "rated_market_actor_id", nullable = false)
    private MarketActor ratedMarketActor;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer score;

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