package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dtos.request.CreateReviewRequest;
import com.skillswap.skillswap.dtos.request.UpdateReviewRequest;
import com.skillswap.skillswap.dtos.response.ApiResponse;
import com.skillswap.skillswap.dtos.response.ReviewResponse;
import com.skillswap.skillswap.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review Management", description = "APIs to manage user reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create a new review")
    public ResponseEntity<ApiResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {
        log.info("📝 Creating review from: {}", authentication.getName());
        String email = authentication.getName();
        ReviewResponse review = reviewService.createReview(request, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(
                        "Review created successfully",
                        true,
                        review
                )
        );
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update an existing review")
    public ResponseEntity<ApiResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request,
            Authentication authentication
    ) {
        log.info("✏️ Updating review ID: {} by: {}", reviewId, authentication.getName());
        String email = authentication.getName();
        ReviewResponse review = reviewService.updateReview(reviewId, request, email);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Review updated successfully",
                        true,
                        review
                )
        );
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<ApiResponse> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        log.info("🗑️ Deleting review ID: {} by: {}", reviewId, authentication.getName());
        String email = authentication.getName();
        reviewService.deleteReview(reviewId, email);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Review deleted successfully",
                        true,
                        null
                )
        );
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get a single review by ID")
    public ResponseEntity<ApiResponse> getReviewById(@PathVariable Long reviewId) {
        log.info("📖 Fetching review ID: {}", reviewId);
        ReviewResponse review = reviewService.getReviewById(reviewId);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Review fetched successfully",
                        true,
                        review
                )
        );
    }

    @GetMapping("/my-reviews")
    @Operation(summary = "Get all reviews written by the current user")
    public ResponseEntity<ApiResponse> getMyReviews(Authentication authentication) {
        log.info("📋 Fetching reviews written by: {}", authentication.getName());
        String email = authentication.getName();
        List<ReviewResponse> reviews = reviewService.getMyReviews(email);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Reviews fetched successfully",
                        true,
                        reviews
                )
        );
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all reviews received by a specific user")
    public ResponseEntity<ApiResponse> getReviewsForUser(@PathVariable Long userId) {
        log.info("📋 Fetching reviews for user ID: {}", userId);
        List<ReviewResponse> reviews = reviewService.getReviewsForUser(userId);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Reviews fetched successfully",
                        true,
                        reviews
                )
        );
    }

    @GetMapping("/match/{matchRequestId}")
    @Operation(summary = "Get all reviews for a specific match")
    public ResponseEntity<ApiResponse> getReviewsForMatch(@PathVariable Long matchRequestId) {
        log.info("📋 Fetching reviews for match ID: {}", matchRequestId);
        List<ReviewResponse> reviews = reviewService.getReviewsForMatch(matchRequestId);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Reviews fetched successfully",
                        true,
                        reviews
                )
        );
    }
}