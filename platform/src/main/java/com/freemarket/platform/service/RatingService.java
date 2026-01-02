package com.freemarket.platform.service;

import com.freemarket.platform.dto.RatingDto;
import com.freemarket.platform.dto.RatingSummaryDto;
import com.freemarket.platform.dto.UserPublicDto;
import com.freemarket.platform.dto.request.RatingRequest;
import com.freemarket.platform.entity.MarketActor;
import com.freemarket.platform.entity.Rating;
import com.freemarket.platform.exception.ForbiddenException;
import com.freemarket.platform.exception.NotFoundException;
import com.freemarket.platform.repository.MarketActorRepository;
import com.freemarket.platform.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MarketActorRepository marketActorRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository, MarketActorRepository marketActorRepository) {
        this.ratingRepository = ratingRepository;
        this.marketActorRepository = marketActorRepository;
    }

    @Transactional
    public RatingDto leaveRating(UUID raterId, UUID rateeId, RatingRequest request) {
        if (raterId.equals(rateeId)) {
            throw new ForbiddenException("You cannot rate yourself");
        }

        MarketActor rater = marketActorRepository.findById(raterId)
                .orElseThrow(() -> new NotFoundException("Rater with id: " + raterId + " not found"));

        MarketActor ratee = marketActorRepository.findById(rateeId)
                .orElseThrow(() -> new NotFoundException("Rate with id: " + rateeId + " not found"));

        Rating rating = ratingRepository.findByRater_IdAndRatee_Id(raterId, rateeId)
                .orElseGet(Rating::new);

        rating.setRater(rater);
        rating.setRatee(ratee);
        rating.setScore(request.score());
        rating.setComment(request.comment());

        Rating saved = ratingRepository.save(rating);
        return toDto(saved);
    }

    @Transactional
    public void deleteMyRating(UUID raterId, UUID rateeId) {
        if (raterId.equals(rateeId)) {
            throw new ForbiddenException("You cannot rate yourself");
        }
        ratingRepository.deleteByRater_IdAndRatee_Id(raterId, rateeId);
    }

    @Transactional
    public Page<RatingDto> listRatingsForUser(UUID rateeId, Pageable pageable) {
        return ratingRepository.findAllWithRaterByRatee_Id(rateeId, pageable).map(this::toDto);
    }

    @Transactional
    public RatingSummaryDto summary(UUID rateeId) {
        Object[] row = ratingRepository.getSummaryForRatee(rateeId);
        Double avg = (Double) row[0];
        Long count = (Long) row[1];
        return new RatingSummaryDto(avg == null ? 0.0 : avg, count == null ? 0L : count);
    }

    private RatingDto toDto(Rating r) {
        UserPublicDto rater = new UserPublicDto(
                r.getRater().getId(),
                r.getRater().getUsername()
        );

        return new RatingDto(
                r.getId(),
                r.getScore(),
                r.getComment(),
                r.getCreatedAt(),
                rater
        );
    }
}
