package com.workspaceflow.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka topic configuration
 */
@Configuration
public class KafkaTopicConfig {

    public static final String WORKFLOW_EVENTS = "workflow.events";
    public static final String TASK_EVENTS = "task.events";
    public static final String NOTIFICATION_EVENTS = "notification.events";
    public static final String AUDIT_EVENTS = "audit.events";
    public static final String DEADLETTER_EVENTS = "deadletter.events";

    @Bean
    public NewTopic workflowEventsTopic() {
        return TopicBuilder.name(WORKFLOW_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic taskEventsTopic() {
        return TopicBuilder.name(TASK_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationEventsTopic() {
        return TopicBuilder.name(NOTIFICATION_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic auditEventsTopic() {
        return TopicBuilder.name(AUDIT_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deadletterEventsTopic() {
        return TopicBuilder.name(DEADLETTER_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
