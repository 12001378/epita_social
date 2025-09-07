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
public class CommentEvent {
    private UUID commentId;
    private UUID postId;
    private UUID profileId;
    private String comment;
    private String authorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private EventType eventType;
    
    public enum EventType {
        COMMENT_CREATED,
        COMMENT_UPDATED,
        COMMENT_DELETED,
        COMMENT_LIKED
    }
}
