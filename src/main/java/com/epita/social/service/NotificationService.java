package com.epita.social.service;

import com.epita.social.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    public void processNotification(NotificationEvent notificationEvent) {
        log.info("Processing notification: {}", notificationEvent);
        
        switch (notificationEvent.getType()) {
            case NEW_FOLLOWER:
                handleNewFollowerNotification(notificationEvent);
                break;
            case POST_LIKED:
                handlePostLikedNotification(notificationEvent);
                break;
            case POST_COMMENTED:
                handlePostCommentedNotification(notificationEvent);
                break;
            case POST_SAVED:
                handlePostSavedNotification(notificationEvent);
                break;
            case COMMENT_LIKED:
                handleCommentLikedNotification(notificationEvent);
                break;
            case PROFILE_MENTIONED:
                handleProfileMentionedNotification(notificationEvent);
                break;
        }
    }

    private void handleNewFollowerNotification(NotificationEvent event) {
        log.info("New follower notification: User {} has a new follower", event.getRecipientId());
        // Save to database, send push notification, email, etc.
    }

    private void handlePostLikedNotification(NotificationEvent event) {
        log.info("Post liked notification: Post {} was liked", event.getRelatedEntityId());
        // Save to database, send push notification
    }

    private void handlePostCommentedNotification(NotificationEvent event) {
        log.info("Post commented notification: Post {} received a comment", event.getRelatedEntityId());
        // Save to database, send push notification
    }

    private void handlePostSavedNotification(NotificationEvent event) {
        log.info("Post saved notification: Post {} was saved", event.getRelatedEntityId());
        // Save to database
    }

    private void handleCommentLikedNotification(NotificationEvent event) {
        log.info("Comment liked notification: Comment {} was liked", event.getRelatedEntityId());
        // Save to database, send push notification
    }

    private void handleProfileMentionedNotification(NotificationEvent event) {
        log.info("Profile mentioned notification: User {} was mentioned", event.getRecipientId());
        // Save to database, send push notification
    }
}
