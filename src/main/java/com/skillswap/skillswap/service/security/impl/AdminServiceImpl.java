package com.skillswap.skillswap.service.security.impl;

import com.skillswap.skillswap.dtos.response.security.UserResponse;
import com.skillswap.skillswap.exception.DataNotFoundException;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.service.security.AdminService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getUsername(),
                        user.getId(),
                        user.getEmail(),
                        user.getRole(),
                        user.getProfile() != null ? user.getProfile().getAvatarUrl() : null
                ))
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id " + id));

        return new UserResponse(
                user.getUsername(),
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getProfile() != null ? user.getProfile().getAvatarUrl() : null
        );
    }

    @Override
    @Transactional
    public void updateUserById(Long id, UserResponse userResponse) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id " + id));

        user.setUsername(userResponse.getUsername());
        user.setEmail(userResponse.getEmail());
        user.setRole(userResponse.getRole());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id " + id));

        if (user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Cannot delete admin users");
        }

        // Step 1: Get all user skill IDs for this user
        List<Long> userSkillIds = entityManager.createQuery(
                        "SELECT us.id FROM UserSkill us WHERE us.user.id = :userId", Long.class)
                .setParameter("userId", id)
                .getResultList();

        // Step 2: Delete match requests that reference these user skills
        if (!userSkillIds.isEmpty()) {
            entityManager.createQuery(
                            "DELETE FROM MatchRequest m WHERE m.fromUserSkill.id IN :skillIds OR m.toUserSkill.id IN :skillIds")
                    .setParameter("skillIds", userSkillIds)
                    .executeUpdate();
        }

        // Step 3: Delete match requests where user is fromUser or toUser
        entityManager.createQuery(
                        "DELETE FROM MatchRequest m WHERE m.fromUser.id = :userId OR m.toUser.id = :userId")
                .setParameter("userId", id)
                .executeUpdate();

        // Step 4: Delete user skills
        entityManager.createQuery("DELETE FROM UserSkill us WHERE us.user.id = :userId")
                .setParameter("userId", id)
                .executeUpdate();

        // ✅ Step 5: Delete attachments first (they reference messages)
        entityManager.createQuery(
                        "DELETE FROM Attachment a WHERE a.message.id IN " +
                                "(SELECT m.id FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId)")
                .setParameter("userId", id)
                .executeUpdate();

        // ✅ Step 6: Delete all messages where user is sender or receiver
        // This is the KEY FIX for the foreign key constraint error
        entityManager.createQuery(
                        "DELETE FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
                .setParameter("userId", id)
                .executeUpdate();

        // Step 7: Delete user profile if exists (handled by cascade, but keeping for safety)
//        entityManager.createQuery("DELETE FROM Profile p WHERE p.user.id = :userId")
//                .setParameter("userId", id)
//                .executeUpdate();

        // Step 8: Finally delete the user
        userRepository.delete(user);
        entityManager.flush();
    }
}