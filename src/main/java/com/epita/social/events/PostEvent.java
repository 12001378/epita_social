package com.epita.social.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEvent {
    private UUID postId;
    private UUID profileId;
    private String authorName;
    private String caption;
    private String location;
    private List<String> mediaUrls;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private EventType eventType;
    
    public enum EventType {
        POST_CREATED,
        POST_UPDATED,
        POST_DELETED,
        POST_ARCHIVED
    }
}
