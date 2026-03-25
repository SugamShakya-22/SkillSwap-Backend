package com.skillswap.skillswap.service.Impl;

import com.skillswap.skillswap.dtos.request.ReactionRequest;
import com.skillswap.skillswap.dtos.request.SendMessageRequest;
import com.skillswap.skillswap.dtos.response.AttachmentResponse;
import com.skillswap.skillswap.dtos.response.MessageResponse;
import com.skillswap.skillswap.exception.ResourceNotFoundException;
import com.skillswap.skillswap.model.Attachment;
import com.skillswap.skillswap.model.Message;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.repository.AttachmentRepository;
import com.skillswap.skillswap.repository.MessageRepository;
import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.service.MessageService;
import com.skillswap.skillswap.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String UPLOAD_DIR = "uploads/messages/";
    private static final String BACKEND_URL = "http://192.168.1.75:8080";

    @Override
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request, String senderEmail) {
        log.info("========================================");
        log.info("📨 SEND MESSAGE REQUEST");
        log.info("   Sender Email: {}", senderEmail);
        log.info("   Receiver ID: {}", request.receiverId());

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", senderEmail));

        User receiver = userRepository.findById(request.receiverId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.receiverId()));

        log.info("✅ Users found:");
        log.info("   Sender: {} (ID: {}, Email: {})", sender.getUsername(), sender.getId(), sender.getEmail());
        log.info("   Receiver: {} (ID: {}, Email: {})", receiver.getUsername(), receiver.getId(), receiver.getEmail());

        boolean hasContent = request.content() != null && !request.content().isBlank();
        boolean hasFiles = request.files() != null && !request.files().isEmpty();

        if (!hasContent && !hasFiles) {
            throw new IllegalArgumentException("Message must have either content or files");
        }

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.content() != null ? request.content() : "")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .attachments(new ArrayList<>())
                .build();

        Message savedMessage = messageRepository.save(message);
        log.info("💾 Message saved with ID: {}", savedMessage.getId());

        if (hasFiles) {
            log.info("📎 Processing {} file(s)...", request.files().size());
            for (MultipartFile file : request.files()) {
                try {
                    String fileUrl = uploadFile(file);
                    log.info("   ✅ Uploaded: {}", file.getOriginalFilename());

                    Attachment attachment = Attachment.builder()
                            .message(savedMessage)
                            .fileName(file.getOriginalFilename())
                            .fileUrl(fileUrl)
                            .mimeType(file.getContentType())
                            .fileSize(file.getSize())
                            .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                            .build();

                    savedMessage.addAttachment(attachment);
                    attachmentRepository.save(attachment);
                } catch (IOException e) {
                    log.error("❌ Failed to upload file: {}", file.getOriginalFilename(), e);
                    throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
                }
            }
        }

        String notificationMessage = createNotificationMessage(sender, hasContent, hasFiles, savedMessage);
        notificationService.createNotification(receiver, "NEW_MESSAGE", notificationMessage, savedMessage.getId());

        MessageResponse response = mapToResponse(savedMessage);

        // ✅ CRITICAL FIX: Broadcast using EMAIL instead of ID
        log.info("========================================");
        log.info("🚀 BROADCASTING VIA WEBSOCKET");
        log.info("   Broadcast Method: convertAndSendToUser");
        log.info("   Using EMAIL as principal identifier");
        log.info("   Sender Email: {}", sender.getEmail());
        log.info("   Receiver Email: {}", receiver.getEmail());

        try {
            // Send to receiver using EMAIL
            log.info("   📤 Sending to RECEIVER...");
            log.info("      Principal: {}", receiver.getEmail());
            log.info("      Destination: /queue/messages");
            messagingTemplate.convertAndSendToUser(
                    receiver.getEmail(),  // ✅ Use email instead of ID
                    "/queue/messages",
                    response
            );
            log.info("   ✅ SENT TO RECEIVER SUCCESSFULLY");

            // Send to sender using EMAIL
            log.info("   📤 Sending to SENDER...");
            log.info("      Principal: {}", sender.getEmail());
            log.info("      Destination: /queue/messages");
            messagingTemplate.convertAndSendToUser(
                    sender.getEmail(),  // ✅ Use email instead of ID
                    "/queue/messages",
                    response
            );
            log.info("   ✅ SENT TO SENDER SUCCESSFULLY");

            log.info("🎉 WEBSOCKET BROADCAST COMPLETED SUCCESSFULLY");

        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ WEBSOCKET BROADCAST FAILED!");
            log.error("   Error: {}", e.getMessage());
            e.printStackTrace();
            log.error("========================================");
        }

        log.info("========================================\n");
        return response;
    }

    @Override
    public List<MessageResponse> getConversation(Long otherUserId, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", otherUserId));

        return messageRepository.findConversation(currentUser, otherUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public MessageResponse markMessageAsRead(Long messageId, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        if (!message.getReceiver().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not allowed to mark this message as read");
        }

        message.setIsRead(true);
        Message saved = messageRepository.save(message);

        MessageResponse response = mapToResponse(saved);

        try {
            // ✅ Use email
            messagingTemplate.convertAndSendToUser(
                    message.getSender().getEmail(),
                    "/queue/read",
                    response
            );
            log.info("✅ Read receipt sent to sender email: {}", message.getSender().getEmail());
        } catch (Exception e) {
            log.error("❌ Failed to send read receipt", e);
        }

        return response;
    }

    @Override
    @Transactional
    public MessageResponse addReaction(ReactionRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", request.messageId()));

        if (!message.getSender().getId().equals(user.getId()) &&
                !message.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not part of this conversation");
        }

        message.setReaction(request.reaction());
        Message saved = messageRepository.save(message);

        MessageResponse response = mapToResponse(saved);

        try {
            // ✅ Use email
            messagingTemplate.convertAndSendToUser(
                    message.getSender().getEmail(),
                    "/queue/messages",
                    response
            );
            messagingTemplate.convertAndSendToUser(
                    message.getReceiver().getEmail(),
                    "/queue/messages",
                    response
            );
            log.info("✅ Reaction update broadcasted");
        } catch (Exception e) {
            log.error("❌ Failed to broadcast reaction", e);
        }

        return response;
    }

    private String createNotificationMessage(User sender, boolean hasContent, boolean hasFiles, Message message) {
        String senderName = sender.getUsername();

        if (hasFiles && !hasContent) {
            if (!message.getAttachments().isEmpty()) {
                String mimeType = message.getAttachments().get(0).getMimeType();
                if (mimeType != null) {
                    if (mimeType.startsWith("image/")) {
                        return senderName + " sent you a photo";
                    } else if (mimeType.startsWith("video/")) {
                        return senderName + " sent you a video";
                    } else {
                        return senderName + " sent you a file";
                    }
                }
            }
            return senderName + " sent you an attachment";
        } else if (hasContent) {
            String content = message.getContent();
            if (content.length() > 50) {
                return senderName + ": " + content.substring(0, 47) + "...";
            } else {
                return senderName + ": " + content;
            }
        } else {
            return senderName + " sent you a message";
        }
    }

    private String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID() + extension;

        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.write(filePath, file.getBytes());

        return BACKEND_URL + "/uploads/messages/" + uniqueFilename;
    }

    private MessageResponse mapToResponse(Message message) {
        List<AttachmentResponse> attachmentResponses = message.getAttachments() != null
                ? message.getAttachments().stream()
                .map(a -> new AttachmentResponse(
                        a.getId(),
                        a.getFileName(),
                        a.getFileUrl(),
                        a.getMimeType(),
                        a.getFileSize()
                ))
                .toList()
                : new ArrayList<>();

        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getContent(),
                message.getIsRead(),
                message.getCreatedAt(),
                message.getReaction(),
                attachmentResponses
        );
    }
}