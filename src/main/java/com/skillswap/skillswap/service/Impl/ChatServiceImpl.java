package com.skillswap.skillswap.service.Impl;

import com.skillswap.skillswap.dtos.response.ChatUserResponse;
import com.skillswap.skillswap.helper.MatchStatus;
import com.skillswap.skillswap.model.MatchRequest;
import com.skillswap.skillswap.model.Message;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.repository.MatchRequestRepository;
import com.skillswap.skillswap.repository.MessageRepository;
import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final MessageRepository messageRepository;

    @Override
    public List<ChatUserResponse> getChatUsers(String currentUserEmail) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MatchStatus> allowedStatuses = List.of(
                MatchStatus.ACCEPTED,
                MatchStatus.COMPLETION_REQUESTED,
                MatchStatus.COMPLETED
        );

        List<MatchRequest> matches =
                matchRequestRepository.findChatEligibleRequests(currentUser, allowedStatuses);

        Map<Long, ChatUserResponse> chatUsers = new HashMap<>();

        for (MatchRequest match : matches) {

            User otherUser =
                    match.getFromUser().equals(currentUser)
                            ? match.getToUser()
                            : match.getFromUser();

            // Avoid duplicates
            if (chatUsers.containsKey(otherUser.getId())) continue;

            Message lastMessage = messageRepository.findLastMessageBetweenUsers(currentUser, otherUser);

            // ✅ Null-safe avatar
            String avatarUrl = otherUser.getProfile() != null
                    ? otherUser.getProfile().getAvatarUrl()
                    : null;

            // ✅ Count unread messages from otherUser to currentUser
            long unreadCount = messageRepository.countByReceiverAndSenderAndIsReadFalse(
                    currentUser,
                    otherUser
            );

            // ✅ Check if other user is online
            boolean isOnline = otherUser.getIsOnline() != null && otherUser.getIsOnline();

            // ✅ IMPROVED: Show better text for attachments
            String lastMessageText = null;
            if (lastMessage != null) {
                if (lastMessage.getAttachments() != null && !lastMessage.getAttachments().isEmpty()) {
                    // Has attachments - show appropriate text
                    boolean isCurrentUserSender = lastMessage.getSender().equals(currentUser);
                    String prefix = isCurrentUserSender ? "You sent: " : "";

                    // Check if it's an image or video
                    String mimeType = lastMessage.getAttachments().get(0).getMimeType();
                    if (mimeType != null) {
                        if (mimeType.startsWith("image/")) {
                            lastMessageText = prefix + "📷 Photo";
                        } else if (mimeType.startsWith("video/")) {
                            lastMessageText = prefix + "🎥 Video";
                        } else {
                            lastMessageText = prefix + "📎 File";
                        }
                    } else {
                        lastMessageText = prefix + "📎 Attachment";
                    }

                    // If there's also text content, show it instead
                    if (lastMessage.getContent() != null && !lastMessage.getContent().trim().isEmpty()) {
                        lastMessageText = lastMessage.getContent();
                    }
                } else {
                    // Text-only message
                    boolean isCurrentUserSender = lastMessage.getSender().equals(currentUser);
                    String prefix = isCurrentUserSender ? "You: " : "";
                    lastMessageText = prefix + lastMessage.getContent();
                }
            }

            chatUsers.put(
                    otherUser.getId(),
                    new ChatUserResponse(
                            otherUser.getId(),
                            otherUser.getUsername(),
                            avatarUrl,
                            lastMessageText,
                            lastMessage != null ? lastMessage.getCreatedAt() : null,
                            isOnline,
                            (int) unreadCount
                    )
            );
        }

        return chatUsers.values()
                .stream()
                .sorted(Comparator.comparing(
                        ChatUserResponse::lastMessageTime,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }
}