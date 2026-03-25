package com.skillswap.skillswap.repository;

import com.skillswap.skillswap.model.Message;
import com.skillswap.skillswap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
        SELECT m FROM Message m
        WHERE (m.sender = :user1 AND m.receiver = :user2)
           OR (m.sender = :user2 AND m.receiver = :user1)
        ORDER BY m.createdAt ASC
    """)
    List<Message> findConversation(User user1, User user2);

    List<Message> findByReceiverAndIsReadFalse(User receiver);

    @Query("""
    SELECT m
    FROM Message m
    WHERE 
      (m.sender = :user1 AND m.receiver = :user2)
       OR
      (m.sender = :user2 AND m.receiver = :user1)
    ORDER BY m.createdAt DESC
    LIMIT 1
""")
    Message findLastMessageBetweenUsers(
            @Param("user1") User user1,
            @Param("user2") User user2
    );

    // ✅ Count unread messages from specific sender to specific receiver
    long countByReceiverAndSenderAndIsReadFalse(User receiver, User sender);

    // ✅ NEW: Delete all messages where user is sender or receiver
    // This is needed when deleting a user to avoid foreign key constraint violations
    @Modifying
    @Query("""
        DELETE FROM Message m 
        WHERE m.sender = :sender OR m.receiver = :receiver
    """)
    void deleteBySenderOrReceiver(
            @Param("sender") User sender,
            @Param("receiver") User receiver
    );
}