package com.workspaceflow.event;

import com.workspaceflow.config.KafkaTopicConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for producing Kafka events
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publish workflow event
     */
    public void publishWorkflowEvent(String type, String workflowId, String instanceId, Map<String, Object> payload) {
        WorkflowEvent event = WorkflowEvent.builder()
                .type(type)
                .workflowId(workflowId)
                .instanceId(instanceId)
                .timestamp(LocalDateTime.now())
                .payload(payload)
                .build();

        sendEvent(KafkaTopicConfig.WORKFLOW_EVENTS, instanceId, event);
    }

    /**
     * Publish task event
     */
    public void publishTaskEvent(String type, String taskId, String assignee, String instanceId,
            Map<String, Object> payload) {
        TaskEvent event = TaskEvent.builder()
                .type(type)
                .taskId(taskId)
                .assignee(assignee)
                .instanceId(instanceId)
                .timestamp(LocalDateTime.now())
                .payload(payload)
                .build();

        sendEvent(KafkaTopicConfig.TASK_EVENTS, taskId, event);
    }

    /**
     * Publish audit event
     */
    public void publishAuditEvent(String eventType, String entityType, String entityId, String userId,
            Map<String, Object> payload) {
        Map<String, Object> auditPayload = Map.of(
                "eventType", eventType,
                "entityType", entityType,
                "entityId", entityId,
                "userId", userId != null ? userId : "system",
                "timestamp", LocalDateTime.now(),
                "payload", payload != null ? payload : Map.of());

        sendEvent(KafkaTopicConfig.AUDIT_EVENTS, entityId, auditPayload);
    }

    /**
     * Send event to Kafka topic
     */
    private void sendEvent(String topic, String key, Object event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event sent successfully to topic: {} with key: {}", topic, key);
            } else {
                log.error("Failed to send event to topic: {} with key: {}", topic, key, ex);
            }
        });
    }
}
