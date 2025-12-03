package com.workspaceflow.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for task events
 */
@Slf4j
@Component
public class TaskEventConsumer {

    @KafkaListener(topics = "task.events", groupId = "task-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskEvent(
            @Payload TaskEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {
            log.info("Received task event from topic {}: type={}, taskId={}",
                    topic, event.getType(), event.getTaskId());

            // Process task event based on type
            switch (event.getType()) {
                case TaskEvent.TASK_CREATED:
                    log.info("Task created: {}", event.getTaskId());
                    break;
                case TaskEvent.TASK_ASSIGNED:
                    log.info("Task assigned to {}: {}", event.getAssignee(), event.getTaskId());
                    break;
                case TaskEvent.TASK_COMPLETED:
                    log.info("Task completed: {}", event.getTaskId());
                    break;
                case TaskEvent.TASK_REJECTED:
                    log.warn("Task rejected: {}", event.getTaskId());
                    break;
                default:
                    log.info("Unknown task event type: {}", event.getType());
            }

        } catch (Exception e) {
            log.error("Error processing task event", e);
            throw e; // Re-throw to trigger retry mechanism
        }
    }
}
