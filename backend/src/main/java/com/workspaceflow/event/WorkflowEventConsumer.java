package com.workspaceflow.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for workflow events
 */
@Slf4j
@Component
public class WorkflowEventConsumer {

    @KafkaListener(topics = "workflow.events", groupId = "workflow-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeWorkflowEvent(
            @Payload WorkflowEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {
            log.info("Received workflow event from topic {}: type={}, instanceId={}",
                    topic, event.getType(), event.getInstanceId());

            // Process workflow event based on type
            switch (event.getType()) {
                case WorkflowEvent.INSTANCE_STARTED:
                    log.info("Workflow instance started: {}", event.getInstanceId());
                    break;
                case WorkflowEvent.INSTANCE_COMPLETED:
                    log.info("Workflow instance completed: {}", event.getInstanceId());
                    break;
                case WorkflowEvent.INSTANCE_FAILED:
                    log.warn("Workflow instance failed: {}", event.getInstanceId());
                    break;
                default:
                    log.info("Unknown workflow event type: {}", event.getType());
            }

        } catch (Exception e) {
            log.error("Error processing workflow event", e);
            throw e; // Re-throw to trigger retry mechanism
        }
    }
}
