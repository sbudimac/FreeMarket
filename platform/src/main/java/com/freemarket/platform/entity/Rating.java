package com.freemarket.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rater_id", nullable = false)
    @NotNull
    private MarketActor rater;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ratee_id", nullable = false)
    @NotNull
    private MarketActor ratee;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    @Max(5)
    private Integer score;

    @Column(columnDefinition = "TEXT")
    @Size(max = 1000)
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Rating() {}

    public Rating(MarketActor rater, MarketActor ratee, Integer score) {
        this.rater = rater;
        this.ratee = ratee;
        this.score = score;
    }

    public Rating(MarketActor rater, MarketActor ratee, Integer score, String comment) {
        this.rater = rater;
        this.ratee = ratee;
        this.score = score;
        this.comment = comment;
    }

    public boolean isSelfRating() {
        return rater != null && ratee != null && rater.getId().equals(ratee.getId());
    }

    public boolean isPerfectScore() {
        return score != null && score == 5;
    }

    public boolean isPoorScore() {
        return score != null && score <= 2;
    }

    public void updateScore(int newScore) {
        this.score = newScore;
    }

    public void updateComment(String newComment) {
        this.comment = newComment;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", rater=" + (rater != null ? rater.getId() : "null") +
                ", ratedMarketActor=" + (ratee != null ? ratee.getId() : "null") +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}