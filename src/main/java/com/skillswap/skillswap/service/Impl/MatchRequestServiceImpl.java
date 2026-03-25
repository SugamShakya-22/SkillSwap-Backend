//package com.skillswap.skillswap.service.Impl;
//
//import com.skillswap.skillswap.dtos.request.CreateMatchRequestRequest;
//import com.skillswap.skillswap.dtos.response.MatchRequestResponse;
//import com.skillswap.skillswap.exception.ResourceNotFoundException;
//import com.skillswap.skillswap.helper.MatchStatus;
//import com.skillswap.skillswap.model.MatchRequest;
//import com.skillswap.skillswap.model.User;
//import com.skillswap.skillswap.model.UserSkill;
//import com.skillswap.skillswap.repository.MatchRequestRepository;
//import com.skillswap.skillswap.repository.UserRepository;
//import com.skillswap.skillswap.repository.UserSkillRepository;
//import com.skillswap.skillswap.service.MatchRequestService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class MatchRequestServiceImpl implements MatchRequestService {
//
//    private final MatchRequestRepository matchRequestRepository;
//    private final UserRepository userRepository;
//    private final UserSkillRepository userSkillRepository;
//
//    // ================= CREATE MATCH REQUEST =================
//    @Override
//    public MatchRequestResponse createMatchRequest(CreateMatchRequestRequest request, Long fromUserId) {
//
//        User fromUser = userRepository.findById(fromUserId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", fromUserId));
//
//        User toUser = userRepository.findById(request.toUserId())
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.toUserId()));
//
//        UserSkill fromSkill = userSkillRepository.findById(request.fromUserSkillId())
//                .orElseThrow(() -> new ResourceNotFoundException("UserSkill", "id", request.fromUserSkillId()));
//
//        UserSkill toSkill = userSkillRepository.findById(request.toUserSkillId())
//                .orElseThrow(() -> new ResourceNotFoundException("UserSkill", "id", request.toUserSkillId()));
//
//        MatchRequest matchRequest = MatchRequest.builder()
//                .fromUser(fromUser)
//                .toUser(toUser)
//                .fromUserSkill(fromSkill)
//                .toUserSkill(toSkill)
//                .status(MatchStatus.PENDING)
//                .build();
//
//        return mapToResponse(matchRequestRepository.save(matchRequest));
//    }
//
//    // ================= GET REQUESTS FOR USER =================
//    @Override
//    public List<MatchRequestResponse> getRequestsForUser(Long userId) {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
//
//        // Get both incoming AND outgoing requests
//        List<MatchRequest> incomingRequests = matchRequestRepository.findByToUser(user);
//        List<MatchRequest> outgoingRequests = matchRequestRepository.findByFromUser(user);
//
//        // Combine both lists
//        List<MatchRequest> allRequests = new ArrayList<>();
//        allRequests.addAll(incomingRequests);
//        allRequests.addAll(outgoingRequests);
//
//        return allRequests.stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//    }
//
//    // ================= ACCEPT / DECLINE =================
//    @Override
//    public MatchRequestResponse respondToRequest(Long requestId, String action, Long userId) {
//
//        MatchRequest request = matchRequestRepository.findById(requestId)
//                .orElseThrow(() -> new ResourceNotFoundException("MatchRequest", "id", requestId));
//
//        // Only receiver can respond
//        if (!request.getToUser().getId().equals(userId)) {
//            throw new RuntimeException("You are not allowed to respond to this request");
//        }
//
//        if (request.getStatus() != MatchStatus.PENDING) {
//            throw new RuntimeException("Request already processed");
//        }
//
//        if ("ACCEPT".equalsIgnoreCase(action)) {
//            request.setStatus(MatchStatus.ACCEPTED);
//        } else if ("DECLINE".equalsIgnoreCase(action)) {
//            request.setStatus(MatchStatus.DECLINED);
//        } else {
//            throw new IllegalArgumentException("Invalid action. Use ACCEPT or DECLINE.");
//        }
//
//        return mapToResponse(matchRequestRepository.save(request));
//    }
//
//    // ================= CANCEL REQUEST =================
//    @Override
//    public void cancelRequest(Long requestId, Long fromUserId) {
//
//        MatchRequest request = matchRequestRepository.findById(requestId)
//                .orElseThrow(() -> new ResourceNotFoundException("MatchRequest", "id", requestId));
//
//        if (!request.getFromUser().getId().equals(fromUserId)) {
//            throw new RuntimeException("You are not allowed to cancel this request");
//        }
//
//        if (request.getStatus() != MatchStatus.PENDING) {
//            throw new RuntimeException("Only pending requests can be cancelled");
//        }
//
//        matchRequestRepository.delete(request);
//    }
//
//    // ================= REQUEST COMPLETION (STEP 1) =================
//    @Override
//    public MatchRequestResponse requestCompletion(Long requestId, Long userId) {
//
//        MatchRequest request = matchRequestRepository.findById(requestId)
//                .orElseThrow(() -> new ResourceNotFoundException("MatchRequest", "id", requestId));
//
//        if (request.getStatus() != MatchStatus.ACCEPTED) {
//            throw new RuntimeException("Only accepted requests can request completion");
//        }
//
//        if (!userId.equals(request.getFromUser().getId()) && !userId.equals(request.getToUser().getId())) {
//            throw new RuntimeException("You are not part of this swap");
//        }
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
//
//        request.setStatus(MatchStatus.COMPLETION_REQUESTED);
//        request.setCompletionRequestedBy(user);
//
//        return mapToResponse(matchRequestRepository.save(request));
//    }
//
//    // ================= CONFIRM COMPLETION (STEP 2) =================
//    @Override
//    public MatchRequestResponse confirmCompletion(Long requestId, Long userId) {
//
//        MatchRequest request = matchRequestRepository.findById(requestId)
//                .orElseThrow(() -> new ResourceNotFoundException("MatchRequest", "id", requestId));
//
//        if (request.getStatus() != MatchStatus.COMPLETION_REQUESTED) {
//            throw new RuntimeException("Completion not requested yet");
//        }
//
//        if (request.getCompletionRequestedBy().getId().equals(userId)) {
//            throw new RuntimeException("You cannot confirm your own completion request");
//        }
//
//        request.setStatus(MatchStatus.COMPLETED);
//        request.setCompletionRequestedBy(null);
//
//        return mapToResponse(matchRequestRepository.save(request));
//    }
//
//    @Override
//    public List<MatchRequestResponse> getAllMatchRequests() {
//        List<MatchRequest> allRequests = matchRequestRepository.findAll();
//
//        return allRequests.stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//    }
//
//    // ================= DTO MAPPER =================
//    private MatchRequestResponse mapToResponse(MatchRequest request) {
//        return new MatchRequestResponse(
//                request.getId(),
//                request.getFromUser().getId(),
//                request.getFromUser().getUsername(),
//                request.getToUser().getId(),
//                request.getToUser().getUsername(),
//                request.getFromUserSkill().getUserSkillId(),
//                request.getFromUserSkill().getSkill().getSkillName(),
//                request.getToUserSkill().getUserSkillId(),
//                request.getToUserSkill().getSkill().getSkillName(),
//                request.getStatus(),
//                request.getCompletionRequestedBy() != null
//                    ? request.getCompletionRequestedBy().getId()
//                    : null
//        );
//    }
//}





package com.skillswap.skillswap.service.Impl;

import com.skillswap.skillswap.dtos.request.CreateMatchRequestRequest;
import com.skillswap.skillswap.dtos.response.MatchRequestResponse;
import com.skillswap.skillswap.exception.ResourceNotFoundException;
import com.skillswap.skillswap.helper.MatchStatus;
import com.skillswap.skillswap.model.MatchRequest;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.model.UserSkill;
import com.skillswap.skillswap.repository.MatchRequestRepository;
import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.repository.UserSkillRepository;
import com.skillswap.skillswap.service.MatchRequestService;
import com.skillswap.skillswap.service.NotificationService; // ✅ ADD THIS
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchRequestServiceImpl implements MatchRequestService {

    private final MatchRequestRepository matchRequestRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final NotificationService notificationService; // ✅ ADD THIS

    // ================= CREATE MATCH REQUEST =================
    @Override
    public MatchRequestResponse createMatchRequest(CreateMatchRequestRequest request, Long fromUserId) {

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", fromUserId));

        User toUser = userRepository.findById(request.toUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.toUserId()));

        UserSkill fromSkill = userSkillRepository.findById(request.fromUserSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("UserSkill", "id", request.fromUserSkillId()));

        UserSkill toSkill = userSkillRepository.findById(request.toUserSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("UserSkill", "id", request.toUserSkillId()));

        MatchRequest matchRequest = MatchRequest.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .fromUserSkill(fromSkill)
                .toUserSkill(toSkill)
                .status(MatchStatus.PENDING)
                .build();

        MatchRequest savedRequest = matchRequestRepository.save(matchRequest);

        // 🔔 CREATE NOTIFICATION FOR RECEIVER
        notificationService.createNotification(
                toUser,
                "SWAP_REQUEST",
                fromUser.getUsername() + " wants to swap " + fromSkill.getSkill().getSkillName() +
                        " for your " + toSkill.getSkill().getSkillName(),
                savedRequest.getId()
        );

        return mapToResponse(savedRequest);
    }

    // ================= GET REQUESTS FOR USER =================
    @Override
    public List<MatchRequestResponse> getRequestsForUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<MatchRequest> incomingRequests = matchRequestRepository.findByToUser(user);
        List<MatchRequest> outgoingRequests = matchRequestRepository.findByFromUser(user);

        List<MatchRequest> allRequests = new ArrayList<>();
        allRequests.addAll(incomingRequests);
        allRequests.addAll(outgoingRequests);

        return allRequests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ================= ACCEPT / DECLINE =================
    @Override
    public MatchRequestResponse respondToRequest(Long requestId, String action, Long userId) {

        MatchRequest request = matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("MatchRequest", "id", requestId));

        if (!request.getToUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to respond to this request");
        }

        if (request.getStatus() != MatchStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        if ("ACCEPT".equalsIgnoreCase(action)) {
            request.setStatus(MatchStatus.ACCEPTED);

            // 🔔 NOTIFY SENDER ABOUT ACCEPTANCE
            notificationService.createNotification(
                    request.getFromUser(),
                    "SWAP_ACCEPTED",
                    request.getToUser().getUsername() + " accepted your swap request!",
                    request.getId()
            );

        } else if ("DECLINE".equalsIgnoreCase(action)) {
            request.setStatus(MatchStatus.DECLINED);

            // 🔔 NOTIFY SENDER ABOUT DECLINE
            notificationService.createNotification(
                    request.getFromUser(),
                    "SWAP_DECLINED",
                    request.getToUser().getUsername() + " declined your swap request",
                    request.getId()
            );

        } else {
            throw new IllegalArgumentException("Invalid action. Use ACCEPT or DECLINE.");
        }

        return mapToResponse(matchRequestRepository.save(request));
    }

    // ================= CANCEL REQUEST =================
    @Override
    public void cancelRequest(Long requestId, Long fromUserId) {

        MatchRequest request = matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("MatchRequest", "id", requestId));

        if (!request.getFromUser().getId().equals(fromUserId)) {
            throw new RuntimeException("You are not allowed to cancel this request");
        }

        if (request.getStatus() != MatchStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be cancelled");
        }

        // 🔔 NOTIFY RECEIVER ABOUT CANCELLATION
        notificationService.createNotification(
                request.getToUser(),
                "SWAP_CANCELLED",
                request.getFromUser().getUsername() + " cancelled their swap request",
                request.getId()
        );

        matchRequestRepository.delete(request);
    }

    // ================= REQUEST COMPLETION (STEP 1) =================
    @Override
    public MatchRequestResponse requestCompletion(Long requestId, Long userId) {

        MatchRequest request = matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("MatchRequest", "id", requestId));

        if (request.getStatus() != MatchStatus.ACCEPTED) {
            throw new RuntimeException("Only accepted requests can request completion");
        }

        if (!userId.equals(request.getFromUser().getId()) && !userId.equals(request.getToUser().getId())) {
            throw new RuntimeException("You are not part of this swap");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        request.setStatus(MatchStatus.COMPLETION_REQUESTED);
        request.setCompletionRequestedBy(user);

        MatchRequest savedRequest = matchRequestRepository.save(request);

        // 🔔 NOTIFY THE OTHER USER
        User otherUser = userId.equals(request.getFromUser().getId())
                ? request.getToUser()
                : request.getFromUser();

        notificationService.createNotification(
                otherUser,
                "SWAP_COMPLETION_REQUESTED",
                user.getUsername() + " marked the swap as completed. Please confirm!",
                request.getId()
        );

        return mapToResponse(savedRequest);
    }

    // ================= CONFIRM COMPLETION (STEP 2) =================
    @Override
    public MatchRequestResponse confirmCompletion(Long requestId, Long userId) {

        MatchRequest request = matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("MatchRequest", "id", requestId));

        if (request.getStatus() != MatchStatus.COMPLETION_REQUESTED) {
            throw new RuntimeException("Completion not requested yet");
        }

        if (request.getCompletionRequestedBy().getId().equals(userId)) {
            throw new RuntimeException("You cannot confirm your own completion request");
        }

        request.setStatus(MatchStatus.COMPLETED);
        User confirmer = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        User requestedBy = request.getCompletionRequestedBy();
        request.setCompletionRequestedBy(null);

        MatchRequest savedRequest = matchRequestRepository.save(request);

        // 🔔 NOTIFY BOTH USERS ABOUT COMPLETION
        notificationService.createNotification(
                requestedBy,
                "SWAP_COMPLETED",
                "Your swap with " + confirmer.getUsername() + " is now complete! 🎉",
                request.getId()
        );

        notificationService.createNotification(
                confirmer,
                "SWAP_COMPLETED",
                "Your swap with " + requestedBy.getUsername() + " is now complete! 🎉",
                request.getId()
        );

        return mapToResponse(savedRequest);
    }

    @Override
    public List<MatchRequestResponse> getAllMatchRequests() {
        List<MatchRequest> allRequests = matchRequestRepository.findAll();

        return allRequests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ================= DTO MAPPER =================
    private MatchRequestResponse mapToResponse(MatchRequest request) {
        return new MatchRequestResponse(
                request.getId(),
                request.getFromUser().getId(),
                request.getFromUser().getUsername(),
                request.getToUser().getId(),
                request.getToUser().getUsername(),
                request.getFromUserSkill().getUserSkillId(),
                request.getFromUserSkill().getSkill().getSkillName(),
                request.getToUserSkill().getUserSkillId(),
                request.getToUserSkill().getSkill().getSkillName(),
                request.getStatus(),
                request.getCompletionRequestedBy() != null
                        ? request.getCompletionRequestedBy().getId()
                        : null
        );
    }
}