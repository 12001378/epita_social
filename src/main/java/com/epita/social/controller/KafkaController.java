package com.epita.social.controller;

import com.epita.social.service.KafkaProducerService;
import com.epita.social.service.KafkaHealthService;
import com.epita.social.events.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaProducerService kafkaProducerService;
    private final KafkaHealthService kafkaHealthService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getKafkaHealth() {
        Map<String, Object> health = new HashMap<>();
        boolean isHealthy = kafkaHealthService.isKafkaHealthy();
        
        health.put("status", isHealthy ? "UP" : "DOWN");
        health.put("timestamp", LocalDateTime.now());
        health.put("clusterInfo", kafkaHealthService.getKafkaClusterInfo());
        
        return ResponseEntity.ok(health);
    }

    @PostMapping("/test/post-event")
    public ResponseEntity<String> testPostEvent() {
        PostEvent postEvent = new PostEvent(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test Author",
            "Test post caption",
            "Test location",
            new ArrayList<>(),
            LocalDateTime.now(),
            PostEvent.EventType.POST_CREATED
        );
        
        kafkaProducerService.sendPostEvent(postEvent);
        return ResponseEntity.ok("Post event sent successfully");
    }

    @PostMapping("/test/comment-event")
    public ResponseEntity<String> testCommentEvent() {
        CommentEvent commentEvent = new CommentEvent(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test comment",
            "Test Author",
            LocalDateTime.now(),
            CommentEvent.EventType.COMMENT_CREATED
        );
        
        kafkaProducerService.sendCommentEvent(commentEvent);
        return ResponseEntity.ok("Comment event sent successfully");
    }

    @PostMapping("/test/like-event")
    public ResponseEntity<String> testLikeEvent() {
        LikeEvent likeEvent = new LikeEvent(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test User",
            LocalDateTime.now(),
            LikeEvent.EventType.POST_LIKED
        );
        
        kafkaProducerService.sendLikeEvent(likeEvent);
        return ResponseEntity.ok("Like event sent successfully");
    }

    @PostMapping("/test/follow-event")
    public ResponseEntity<String> testFollowEvent() {
        FollowEvent followEvent = new FollowEvent(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Follower Name",
            "Followed Name",
            LocalDateTime.now(),
            FollowEvent.EventType.USER_FOLLOWED
        );
        
        kafkaProducerService.sendFollowEvent(followEvent);
        return ResponseEntity.ok("Follow event sent successfully");
    }

    @PostMapping("/test/notification-event")
    public ResponseEntity<String> testNotificationEvent() {
        NotificationEvent notificationEvent = new NotificationEvent(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test notification message",
            NotificationEvent.NotificationType.POST_LIKED,
            UUID.randomUUID(),
            LocalDateTime.now()
        );
        
        kafkaProducerService.sendNotificationEvent(notificationEvent);
        return ResponseEntity.ok("Notification event sent successfully");
    }
}
