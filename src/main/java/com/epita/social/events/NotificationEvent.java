package com.epita.social.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    private UUID recipientId;
    private UUID triggeredById;
    private String message;
    private NotificationType type;
    private UUID relatedEntityId; // Could be postId, commentId, etc.
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    public enum NotificationType {
        NEW_FOLLOWER,
        POST_LIKED,
        POST_COMMENTED,
        POST_SAVED,
        COMMENT_LIKED,
        PROFILE_MENTIONED
    }
}
