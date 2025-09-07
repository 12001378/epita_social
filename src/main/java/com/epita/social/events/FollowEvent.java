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
public class FollowEvent {
    private UUID followerId;
    private UUID followedId;
    private String followerName;
    private String followedName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private EventType eventType;
    
    public enum EventType {
        USER_FOLLOWED,
        USER_UNFOLLOWED
    }
}
