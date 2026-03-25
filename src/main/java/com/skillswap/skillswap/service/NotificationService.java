package com.skillswap.skillswap.service;

import com.skillswap.skillswap.dtos.response.NotificationResponse;
import com.skillswap.skillswap.model.User;

import java.util.List;

public interface NotificationService {

    // Create a new notification
    void createNotification(User user, String type, String message, Long relatedEntityId);

    // Get all notifications for a user
    List<NotificationResponse> getUserNotifications(Long userId);

    // Get unread notifications
    List<NotificationResponse> getUnreadNotifications(Long userId);

    // Mark notification as read
    void markAsRead(Long notificationId, Long userId);

    // Mark all as read
    void markAllAsRead(Long userId);

    // Get unread count
    Long getUnreadCount(Long userId);
}