package com.skillswap.skillswap.service.Impl;

import com.skillswap.skillswap.dtos.request.CreateReviewRequest;
import com.skillswap.skillswap.dtos.request.UpdateReviewRequest;
import com.skillswap.skillswap.dtos.response.ReviewResponse;
import com.skillswap.skillswap.exception.ConflictException;
import com.skillswap.skillswap.exception.ResourceNotFoundException;
import com.skillswap.skillswap.helper.MatchStatus;
import com.skillswap.skillswap.model.MatchRequest;
import com.skillswap.skillswap.model.Profile;
import com.skillswap.skillswap.model.Review;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.repository.MatchRequestRepository;
import com.skillswap.skillswap.repository.ProfileRepository;
import com.skillswap.skillswap.repository.ReviewRepository;
import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.service.NotificationService;
import com.skillswap.skillswap.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final ProfileRepository profileRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request, String reviewerEmail) {
        // Get reviewer
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", reviewerEmail));

        // Get reviewee
        User reviewee = userRepository.findById(request.revieweeId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.revieweeId()));

        // Get match request
        MatchRequest matchRequest = matchRequestRepository.findById(request.matchRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("MatchRequest", "id", request.matchRequestId()));

        // Validation 1: Reviewer cannot review themselves
        if (reviewer.getId().equals(reviewee.getId())) {
            throw new IllegalArgumentException("You cannot review yourself");
        }

        // Validation 2: Match must be COMPLETED
        if (matchRequest.getStatus() != MatchStatus.COMPLETED) {
            throw new IllegalArgumentException("You can only review completed matches");
        }

        // Validation 3: Both users must be part of the match
        boolean reviewerInMatch = matchRequest.getFromUser().getId().equals(reviewer.getId()) ||
                matchRequest.getToUser().getId().equals(reviewer.getId());
        boolean revieweeInMatch = matchRequest.getFromUser().getId().equals(reviewee.getId()) ||
                matchRequest.getToUser().getId().equals(reviewee.getId());

        if (!reviewerInMatch || !revieweeInMatch) {
            throw new IllegalArgumentException("Both users must be part of this match");
        }

        // Validation 4: Check if review already exists
        if (reviewRepository.existsByReviewerAndRevieweeAndMatchRequest(reviewer, reviewee, matchRequest)) {
            throw new ConflictException("You have already reviewed this user for this match");
        }

        // Create review
        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .matchRequest(matchRequest)
                .comment(request.comment())
                .build();

        Review savedReview = reviewRepository.save(review);

        // Update total reviews count in reviewee's profile
        updateReviewCount(reviewee);

        // Create notification for reviewee
        notificationService.createNotification(
                reviewee,
                "NEW_REVIEW",
                reviewer.getUsername() + " left you a review",
                savedReview.getId()
        );

        log.info("✅ Review created: {} reviewed {}", reviewer.getUsername(), reviewee.getUsername());

        return mapToResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request, String reviewerEmail) {
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", reviewerEmail));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        // Only the reviewer can update their review
        if (!review.getReviewer().getId().equals(reviewer.getId())) {
            throw new IllegalArgumentException("You can only update your own reviews");
        }

        // Update comment
        review.setComment(request.comment());
        Review updatedReview = reviewRepository.save(review);

        log.info("✅ Review updated: ID {}", reviewId);

        return mapToResponse(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, String reviewerEmail) {
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", reviewerEmail));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        // Only the reviewer can delete their review
        if (!review.getReviewer().getId().equals(reviewer.getId())) {
            throw new IllegalArgumentException("You can only delete your own reviews");
        }

        User reviewee = review.getReviewee();
        reviewRepository.delete(review);

        // Update total reviews count in reviewee's profile
        updateReviewCount(reviewee);

        log.info("✅ Review deleted: ID {}", reviewId);
    }

    @Override
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        return mapToResponse(review);
    }

    @Override
    public List<ReviewResponse> getMyReviews(String reviewerEmail) {
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", reviewerEmail));

        return reviewRepository.findByReviewer(reviewer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return reviewRepository.findByRevieweeWithDetails(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsForMatch(Long matchRequestId) {
        MatchRequest matchRequest = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("MatchRequest", "id", matchRequestId));

        return reviewRepository.findByMatchRequest(matchRequest)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update the total review count in the user's profile
     */
    private void updateReviewCount(User user) {
        long reviewCount = reviewRepository.countByReviewee(user);

        Profile profile = profileRepository.findByUser(user)
                .orElse(null);

        if (profile != null) {
            profile.setTotalReviews((int) reviewCount);
            profileRepository.save(profile);
        }
    }

    /**
     * Map Review entity to ReviewResponse DTO
     */
    private ReviewResponse mapToResponse(Review review) {
        // Get reviewer profile for avatar
        Profile reviewerProfile = profileRepository.findByUser(review.getReviewer())
                .orElse(null);

        return new ReviewResponse(
                review.getId(),
                review.getReviewer().getId(),
                review.getReviewer().getUsername(),
                reviewerProfile != null ? reviewerProfile.getAvatarUrl() : null,
                review.getReviewee().getId(),
                review.getReviewee().getUsername(),
                review.getMatchRequest().getId(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}