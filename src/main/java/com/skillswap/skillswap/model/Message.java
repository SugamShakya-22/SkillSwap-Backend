//package com.skillswap.skillswap.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "messages")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Message {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "sender_id", nullable = false)
//    private User sender;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "receiver_id", nullable = false)
//    private User receiver;
//
//    @Column(nullable = false, length = 1000)
//    private String content;
//
//    @Builder.Default
//    private Boolean isRead = false;
//
//    private LocalDateTime createdAt;
//}






package com.skillswap.skillswap.model;

import jakarta.persistence.*;
        import lombok.*;

        import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_sender"))
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_receiver"))
    private User receiver;

    @Column(nullable = false, length = 1000)
    private String content;

    @Builder.Default
    private Boolean isRead = false;

    private LocalDateTime createdAt;

    // ✅ NEW: Support for reactions
    @Column(length = 10)
    private String reaction;

    // ✅ NEW: Support for attachments (one-to-many relationship)
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Attachment> attachments = new ArrayList<>();

    // Helper method to add attachment
    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
        attachment.setMessage(this);
    }

    // Helper method to remove attachment
    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
        attachment.setMessage(null);
    }
}