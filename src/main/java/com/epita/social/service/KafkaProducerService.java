package com.epita.social.service;

import com.epita.social.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String POST_TOPIC = "post-events";
    private static final String COMMENT_TOPIC = "comment-events";
    private static final String LIKE_TOPIC = "like-events";
    private static final String FOLLOW_TOPIC = "follow-events";
    private static final String NOTIFICATION_TOPIC = "notification-events";

    public void sendPostEvent(PostEvent postEvent) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(POST_TOPIC, postEvent.getPostId().toString(), postEvent);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Post event sent successfully: {}", postEvent);
                } else {
                    log.error("Failed to send post event: {}", postEvent, ex);
                }
            });
        } catch (Exception ex) {
            log.error("Error sending post event: {}", postEvent, ex);
        }
    }

    public void sendCommentEvent(CommentEvent commentEvent) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(COMMENT_TOPIC, commentEvent.getCommentId().toString(), commentEvent);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Comment event sent successfully: {}", commentEvent);
                } else {
                    log.error("Failed to send comment event: {}", commentEvent, ex);
                }
            });
        } catch (Exception ex) {
            log.error("Error sending comment event: {}", commentEvent, ex);
        }
    }

    public void sendLikeEvent(LikeEvent likeEvent) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(LIKE_TOPIC, likeEvent.getPostId().toString(), likeEvent);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Like event sent successfully: {}", likeEvent);
                } else {
                    log.error("Failed to send like event: {}", likeEvent, ex);
                }
            });
        } catch (Exception ex) {
            log.error("Error sending like event: {}", likeEvent, ex);
        }
    }

    public void sendFollowEvent(FollowEvent followEvent) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(FOLLOW_TOPIC, followEvent.getFollowerId().toString(), followEvent);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Follow event sent successfully: {}", followEvent);
                } else {
                    log.error("Failed to send follow event: {}", followEvent, ex);
                }
            });
        } catch (Exception ex) {
            log.error("Error sending follow event: {}", followEvent, ex);
        }
    }

    public void sendNotificationEvent(NotificationEvent notificationEvent) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(NOTIFICATION_TOPIC, notificationEvent.getRecipientId().toString(), notificationEvent);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Notification event sent successfully: {}", notificationEvent);
                } else {
                    log.error("Failed to send notification event: {}", notificationEvent, ex);
                }
            });
        } catch (Exception ex) {
            log.error("Error sending notification event: {}", notificationEvent, ex);
        }
    }
}
