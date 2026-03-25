package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dtos.request.CreateMatchRequestRequest;
import com.skillswap.skillswap.dtos.response.ApiResponse;
import com.skillswap.skillswap.dtos.response.MatchRequestResponse;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.service.MatchRequestService;
import com.skillswap.skillswap.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
@Tag(name = "Match Requests", description = "APIs to request and respond to skill swaps")
public class MatchRequestController {

    private final MatchRequestService matchRequestService;
    private final UserService userService;

    // ================= CREATE MATCH REQUEST =================
    @PostMapping("/request")
    @Operation(summary = "Create a match/swap request")
    public ResponseEntity<ApiResponse> createRequest(
            @Valid @RequestBody CreateMatchRequestRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long fromUserId = user.getId();

        MatchRequestResponse response = matchRequestService.createMatchRequest(request, fromUserId);
        return ResponseEntity.ok(new ApiResponse("Match request created successfully", true, response));
    }

    // ================= GET REQUESTS FOR USER =================
    @GetMapping("/requests")
    @Operation(summary = "Get all requests sent to the authenticated user")
    public ResponseEntity<ApiResponse> getRequests(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long userId = user.getId();

        List<MatchRequestResponse> responses = matchRequestService.getRequestsForUser(userId);
        return ResponseEntity.ok(new ApiResponse("Fetched match requests successfully", true, responses));
    }

    // ================= ACCEPT / DECLINE =================
    @PutMapping("/requests/{id}")
    @Operation(summary = "Respond to a match request (ACCEPT or DECLINE)")
    public ResponseEntity<ApiResponse> respondRequest(
            @PathVariable Long id,
            @RequestParam String action,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long userId = user.getId();

        MatchRequestResponse response = matchRequestService.respondToRequest(id, action, userId);
        return ResponseEntity.ok(new ApiResponse("Match request updated successfully", true, response));
    }

    // ================= CANCEL REQUEST =================
    @DeleteMapping("/requests/{id}")
    @Operation(summary = "Cancel a match request sent by the authenticated user")
    public ResponseEntity<ApiResponse> cancelRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long userId = user.getId();

        matchRequestService.cancelRequest(id, userId);
        return ResponseEntity.ok(new ApiResponse("Match request cancelled successfully", true));
    }

    // ================= REQUEST COMPLETION (STEP 1) =================
    @PutMapping("/requests/{id}/complete")
    @Operation(summary = "Mark a match as completed (first user)")
    public ResponseEntity<ApiResponse> requestCompletion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long userId = user.getId();

        MatchRequestResponse response = matchRequestService.requestCompletion(id, userId);
        return ResponseEntity.ok(new ApiResponse("Completion requested successfully", true, response));
    }

    // ================= CONFIRM COMPLETION (STEP 2) =================
    @PutMapping("/requests/{id}/confirm")
    @Operation(summary = "Confirm completion by the other user")
    public ResponseEntity<ApiResponse> confirmCompletion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long userId = user.getId();

        MatchRequestResponse response = matchRequestService.confirmCompletion(id, userId);
        return ResponseEntity.ok(new ApiResponse("Completion confirmed successfully", true, response));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/all")
    @Operation(summary = "Get all match requests in the system (Admin only)")
    public ResponseEntity<ApiResponse> getAllMatchRequestsForAdmin() {
        List<MatchRequestResponse> responses = matchRequestService.getAllMatchRequests();
        return ResponseEntity.ok(new ApiResponse("Fetched all match requests successfully", true, responses));
    }
}
