package com.skillswap.skillswap.service;

import com.skillswap.skillswap.dtos.request.CreateMatchRequestRequest;
import com.skillswap.skillswap.dtos.response.MatchRequestResponse;

import java.util.List;

public interface MatchRequestService {

    // Create a swap request
    MatchRequestResponse createMatchRequest(CreateMatchRequestRequest request, Long fromUserId);

    // Get all requests sent TO a specific user
    List<MatchRequestResponse> getRequestsForUser(Long userId);

    // ACCEPT / DECLINE a request (receiver only)
    MatchRequestResponse respondToRequest(Long requestId, String action, Long userId);

    // Cancel a request (sender only, only if pending)
    void cancelRequest(Long requestId, Long fromUserId);

    // Step 1: User requests completion of an accepted swap
    MatchRequestResponse requestCompletion(Long requestId, Long userId);

    // Step 2: Other user confirms completion
    MatchRequestResponse confirmCompletion(Long requestId, Long userId);

    List<MatchRequestResponse> getAllMatchRequests();
}
