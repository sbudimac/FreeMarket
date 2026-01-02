package com.freemarket.platform.controller;

import com.freemarket.platform.dto.RatingDto;
import com.freemarket.platform.dto.RatingSummaryDto;
import com.freemarket.platform.dto.request.RatingRequest;
import com.freemarket.platform.security.CurrentUserRetreiver;
import com.freemarket.platform.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PutMapping("/{rateeId}/rating")
    public RatingDto leaveRating(@PathVariable UUID rateeId,
                                 @Valid @RequestBody RatingRequest request,
                                 Authentication auth) {
        UUID raterId = CurrentUserRetreiver.getCurrentUserID(auth);
        return ratingService.leaveRating(raterId, rateeId, request);
    }

    @DeleteMapping("/{rateeId}/rating")
    public void deleteMyRating(@PathVariable UUID rateeId, Authentication auth) {
        UUID raterId = CurrentUserRetreiver.getCurrentUserID(auth);
        ratingService.deleteMyRating(raterId, rateeId);
    }

    @GetMapping("/{rateeId}/ratings")
    public Page<RatingDto> showRatings(@PathVariable UUID rateeId, Pageable pageable) {
        return ratingService.listRatingsForUser(rateeId, pageable);
    }

    @GetMapping("/{rateeId}/ratings/summary")
    public RatingSummaryDto summary(@PathVariable UUID rateeId) {
        return ratingService.summary(rateeId);
    }
}
