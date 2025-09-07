package com.epita.social.service;

import com.epita.social.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final NotificationService notificationService;

    @KafkaListener(topics = "post-events", groupId = "epita-social-group")
    public void handlePostEvent(@Payload PostEvent postEvent, 
                               @Header(KafkaHeaders.RECEIVED_KEY) String key,
                               Acknowledgment acknowledgment) {
        try {
            log.info("Received post event: {}", postEvent);
            
            switch (postEvent.getEventType()) {
                case POST_CREATED:
                    handlePostCreated(postEvent);
                    break;
                case POST_UPDATED:
                    handlePostUpdated(postEvent);
                    break;
                case POST_DELETED:
                    handlePostDeleted(postEvent);
                    break;
                case POST_ARCHIVED:
                    handlePostArchived(postEvent);
                    break;
            }
            
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Error processing post event: {}", postEvent, ex);
            // Handle error - could implement retry logic or dead letter queue
        }
    }

    @KafkaListener(topics = "comment-events", groupId = "epita-social-group")
    public void handleCommentEvent(@Payload CommentEvent commentEvent,
                                  @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                  Acknowledgment acknowledgment) {
        try {
            log.info("Received comment event: {}", commentEvent);
            
            switch (commentEvent.getEventType()) {
                case COMMENT_CREATED:
                    handleCommentCreated(commentEvent);
                    break;
                case COMMENT_LIKED:
                    handleCommentLiked(commentEvent);
                    break;
                case COMMENT_UPDATED:
                    handleCommentUpdated(commentEvent);
                    break;
                case COMMENT_DELETED:
                    handleCommentDeleted(commentEvent);
                    break;
            }
            
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Error processing comment event: {}", commentEvent, ex);
        }
    }

    @KafkaListener(topics = "like-events", groupId = "epita-social-group")
    public void handleLikeEvent(@Payload LikeEvent likeEvent,
                               @Header(KafkaHeaders.RECEIVED_KEY) String key,
                               Acknowledgment acknowledgment) {
        try {
            log.info("Received like event: {}", likeEvent);
            
            switch (likeEvent.getEventType()) {
                case POST_LIKED:
                    handlePostLiked(likeEvent);
                    break;
                case POST_UNLIKED:
                    handlePostUnliked(likeEvent);
                    break;
            }
            
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Error processing like event: {}", likeEvent, ex);
        }
    }

    @KafkaListener(topics = "follow-events", groupId = "epita-social-group")
    public void handleFollowEvent(@Payload FollowEvent followEvent,
                                 @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                 Acknowledgment acknowledgment) {
        try {
            log.info("Received follow event: {}", followEvent);
            
            switch (followEvent.getEventType()) {
                case USER_FOLLOWED:
                    handleUserFollowed(followEvent);
                    break;
                case USER_UNFOLLOWED:
                    handleUserUnfollowed(followEvent);
                    break;
            }
            
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Error processing follow event: {}", followEvent, ex);
        }
    }

    @KafkaListener(topics = "notification-events", groupId = "epita-social-group")
    public void handleNotificationEvent(@Payload NotificationEvent notificationEvent,
                                       @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                       Acknowledgment acknowledgment) {
        try {
            log.info("Received notification event: {}", notificationEvent);
            
            // Process notification - save to database, send push notification, etc.
            notificationService.processNotification(notificationEvent);
            
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Error processing notification event: {}", notificationEvent, ex);
        }
    }

    // Helper methods for handling specific events
    private void handlePostCreated(PostEvent postEvent) {
        log.info("Processing post created: {}", postEvent.getPostId());
        // Add analytics, content moderation, etc.
    }

    private void handlePostUpdated(PostEvent postEvent) {
        log.info("Processing post updated: {}", postEvent.getPostId());
    }

    private void handlePostDeleted(PostEvent postEvent) {
        log.info("Processing post deleted: {}", postEvent.getPostId());
    }

    private void handlePostArchived(PostEvent postEvent) {
        log.info("Processing post archived: {}", postEvent.getPostId());
    }

    private void handleCommentCreated(CommentEvent commentEvent) {
        log.info("Processing comment created: {}", commentEvent.getCommentId());
        // Trigger notification to post owner
    }

    private void handleCommentLiked(CommentEvent commentEvent) {
        log.info("Processing comment liked: {}", commentEvent.getCommentId());
    }

    private void handleCommentUpdated(CommentEvent commentEvent) {
        log.info("Processing comment updated: {}", commentEvent.getCommentId());
    }

    private void handleCommentDeleted(CommentEvent commentEvent) {
        log.info("Processing comment deleted: {}", commentEvent.getCommentId());
    }

    private void handlePostLiked(LikeEvent likeEvent) {
        log.info("Processing post liked: {}", likeEvent.getPostId());
    }

    private void handlePostUnliked(LikeEvent likeEvent) {
        log.info("Processing post unliked: {}", likeEvent.getPostId());
    }

    private void handleUserFollowed(FollowEvent followEvent) {
        log.info("Processing user followed: {} followed {}", followEvent.getFollowerName(), followEvent.getFollowedName());
    }

    private void handleUserUnfollowed(FollowEvent followEvent) {
        log.info("Processing user unfollowed: {} unfollowed {}", followEvent.getFollowerName(), followEvent.getFollowedName());
    }
}
