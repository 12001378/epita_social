# Kafka Integration for EPITA Social

This document describes the Kafka integration implemented in the EPITA Social application.

## Overview

Kafka has been integrated to handle asynchronous event processing for various social media activities including:
- Post creation, updates, and deletion
- Comments on posts
- Like/Unlike actions
- Follow/Unfollow actions
- Real-time notifications

## Architecture

### Event Types
1. **PostEvent** - Handles post lifecycle events
2. **CommentEvent** - Handles comment-related events
3. **LikeEvent** - Handles like/unlike events
4. **FollowEvent** - Handles follow/unfollow events
5. **NotificationEvent** - Handles notification delivery

### Kafka Topics
- `post-events` - Post-related events
- `comment-events` - Comment-related events
- `like-events` - Like/unlike events
- `follow-events` - Follow/unfollow events
- `notification-events` - Notification events

## Setup Instructions

### 1. Start Kafka with Docker Compose

```bash
docker-compose up -d
```

This will start:
- Zookeeper on port 2181
- Kafka on port 9092
- Kafka UI on port 8090

### 2. Access Kafka UI

Navigate to http://localhost:8090 to view:
- Topics and their configurations
- Messages in topics
- Consumer groups
- Broker information

### 3. Configuration

Kafka configuration is in `application.yml`:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: epita-social-group
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

## Event Flow

### Post Creation
1. User creates a post via REST API
2. Post is saved to database
3. PostEvent is published to `post-events` topic
4. Consumer processes the event for analytics, content moderation, etc.

### Comment Creation
1. User comments on a post
2. Comment is saved to database
3. CommentEvent is published to `comment-events` topic
4. NotificationEvent is published to notify post owner

### Like/Unlike Actions
1. User likes/unlikes a post
2. Database is updated
3. LikeEvent is published to `like-events` topic
4. NotificationEvent is sent to post owner (if different user)

### Follow/Unfollow Actions
1. User follows/unfollows another user
2. Database relationships are updated
3. FollowEvent is published to `follow-events` topic
4. NotificationEvent is sent to followed user

## Testing

### API Endpoints for Testing
- `POST /api/v1/kafka/test/post-event` - Test post event
- `POST /api/v1/kafka/test/comment-event` - Test comment event
- `POST /api/v1/kafka/test/like-event` - Test like event
- `POST /api/v1/kafka/test/follow-event` - Test follow event
- `POST /api/v1/kafka/test/notification-event` - Test notification event

### Monitoring
1. Check Kafka UI at http://localhost:8090
2. View application logs for Kafka producer/consumer activities
3. Monitor topic message counts and consumer lag

## Error Handling

- Failed message processing is logged
- Consider implementing dead letter queues for production
- Retry mechanisms can be added for transient failures

## Production Considerations

1. **Scaling**: Increase topic partitions for higher throughput
2. **Replication**: Set replication factor > 1 for fault tolerance
3. **Monitoring**: Implement proper monitoring and alerting
4. **Security**: Enable SSL/SASL for secure communication
5. **Persistence**: Configure appropriate retention policies

## Troubleshooting

### Common Issues
1. **Connection refused**: Ensure Kafka is running
2. **Serialization errors**: Check event class structure
3. **Consumer lag**: Monitor and scale consumers if needed

### Logs to Check
- Application logs for producer/consumer errors
- Kafka broker logs in Docker containers
- Zookeeper logs for coordination issues

## Future Enhancements

1. **Real-time notifications**: WebSocket integration with Kafka
2. **Analytics**: Stream processing with Kafka Streams
3. **Content moderation**: AI-powered content filtering
4. **Feed generation**: Real-time feed updates
5. **Metrics**: Custom metrics collection and reporting
