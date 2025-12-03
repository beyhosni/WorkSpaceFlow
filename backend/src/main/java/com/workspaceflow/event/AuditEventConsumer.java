package com.workspaceflow.event;

import com.workspaceflow.model.entity.AuditEvent;
import com.workspaceflow.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Kafka consumer for audit events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditEventRepository auditEventRepository;

    @KafkaListener(topics = "audit.events", groupId = "audit-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeAuditEvent(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {
            log.info("Received audit event from topic {}: {}", topic, event);

            // Save audit event to database
            AuditEvent auditEvent = AuditEvent.builder()
                    .eventType((String) event.get("eventType"))
                    .entityType((String) event.get("entityType"))
                    .entityId((String) event.get("entityId"))
                    .userId((String) event.get("userId"))
                    .payload((Map<String, Object>) event.get("payload"))
                    .timestamp(LocalDateTime.now())
                    .build();

            auditEventRepository.save(auditEvent);
            log.info("Audit event saved successfully");

        } catch (Exception e) {
            log.error("Error processing audit event", e);
            throw e; // Re-throw to trigger retry mechanism
        }
    }
}
