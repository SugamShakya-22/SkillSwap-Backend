package com.skillswap.skillswap.service;

import com.skillswap.skillswap.dtos.request.ReactionRequest;
import com.skillswap.skillswap.dtos.request.SendMessageRequest;
import com.skillswap.skillswap.dtos.response.MessageResponse;

import java.util.List;

public interface MessageService {

    MessageResponse sendMessage(SendMessageRequest request, String senderEmail);

    List<MessageResponse> getConversation(Long otherUserId, String currentUserEmail);

    MessageResponse markMessageAsRead(Long messageId, String currentUserEmail);

    // ✅ NEW: Add reaction to message
    MessageResponse addReaction(ReactionRequest request, String userEmail);
}