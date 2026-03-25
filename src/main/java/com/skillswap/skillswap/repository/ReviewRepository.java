package com.skillswap.skillswap.repository;

import com.skillswap.skillswap.model.MatchRequest;
import com.skillswap.skillswap.model.Review;
import com.skillswap.skillswap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Find all reviews written by a specific user
    List<Review> findByReviewer(User reviewer);

    // Find all reviews received by a specific user
    List<Review> findByReviewee(User reviewee);

    // Find review by reviewer, reviewee, and match request
    Optional<Review> findByReviewerAndRevieweeAndMatchRequest(User reviewer, User reviewee, MatchRequest matchRequest);

    // Check if a review already exists for a specific match
    boolean existsByReviewerAndRevieweeAndMatchRequest(User reviewer, User reviewee, MatchRequest matchRequest);

    // Get all reviews for a specific match request
    List<Review> findByMatchRequest(MatchRequest matchRequest);

    // Count total reviews received by a user
    long countByReviewee(User reviewee);

    // Get reviews with user details (custom query for optimization)
    @Query("SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.reviewer " +
            "LEFT JOIN FETCH r.reviewee " +
            "WHERE r.reviewee = :reviewee " +
            "ORDER BY r.createdAt DESC")
    List<Review> findByRevieweeWithDetails(@Param("reviewee") User reviewee);
}