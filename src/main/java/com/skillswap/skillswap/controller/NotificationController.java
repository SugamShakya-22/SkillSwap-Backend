package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dtos.response.ApiResponse;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.service.NotificationService;
import com.skillswap.skillswap.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "APIs for user notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    // ================= GET ALL NOTIFICATIONS =================
    @GetMapping
    @Operation(summary = "Get all notifications for current user")
    public ResponseEntity<ApiResponse> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long userId = user.getId();

        return ResponseEntity.ok(new ApiResponse(
                "Notifications fetched successfully",
                true,
                notificationService.getUserNotifications(userId)
        ));
    }

    // ================= GET UNREAD NOTIFICATIONS =================
    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications")
    public ResponseEntity<ApiResponse> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long userId = user.getId();

        return ResponseEntity.ok(new ApiResponse(
                "Unread notifications fetched successfully",
                true,
                notificationService.getUnreadNotifications(userId)
        ));
    }

    // ================= GET UNREAD COUNT =================
    @GetMapping("/unread/count")
    @Operation(summary = "Get count of unread notifications")
    public ResponseEntity<ApiResponse> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long userId = user.getId();

        return ResponseEntity.ok(new ApiResponse(
                "Unread count fetched successfully",
                true,
                notificationService.getUnreadCount(userId)
        ));
    }

    // ================= MARK AS READ =================
    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<ApiResponse> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long userId = user.getId();

        notificationService.markAsRead(id, userId);

        return ResponseEntity.ok(new ApiResponse(
                "Notification marked as read",
                true,
                null
        ));
    }

    // ================= MARK ALL AS READ =================
    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long userId = user.getId();

        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(new ApiResponse(
                "All notifications marked as read",
                true,
                null
        ));
    }
}