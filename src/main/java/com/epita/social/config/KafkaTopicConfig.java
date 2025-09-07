package com.epita.social.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postEventTopic() {
        return new NewTopic("post-events", 3, (short) 1);
    }

    @Bean
    public NewTopic commentEventTopic() {
        return new NewTopic("comment-events", 3, (short) 1);
    }

    @Bean
    public NewTopic likeEventTopic() {
        return new NewTopic("like-events", 3, (short) 1);
    }

    @Bean
    public NewTopic followEventTopic() {
        return new NewTopic("follow-events", 3, (short) 1);
    }

    @Bean
    public NewTopic notificationEventTopic() {
        return new NewTopic("notification-events", 3, (short) 1);
    }
}
