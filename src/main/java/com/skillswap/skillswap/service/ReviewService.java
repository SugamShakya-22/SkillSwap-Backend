package com.skillswap.skillswap.service;

import com.skillswap.skillswap.dtos.request.CreateReviewRequest;
import com.skillswap.skillswap.dtos.request.UpdateReviewRequest;
import com.skillswap.skillswap.dtos.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    /**
     * Create a new review
     */
    ReviewResponse createReview(CreateReviewRequest request, String reviewerEmail);

    /**
     * Update an existing review (only the reviewer can update)
     */
    ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request, String reviewerEmail);

    /**
     * Delete a review (only the reviewer can delete)
     */
    void deleteReview(Long reviewId, String reviewerEmail);

    /**
     * Get a single review by ID
     */
    ReviewResponse getReviewById(Long reviewId);

    /**
     * Get all reviews written by the current user
     */
    List<ReviewResponse> getMyReviews(String reviewerEmail);

    /**
     * Get all reviews received by a specific user
     */
    List<ReviewResponse> getReviewsForUser(Long userId);

    /**
     * Get reviews for a specific match request
     */
    List<ReviewResponse> getReviewsForMatch(Long matchRequestId);
}