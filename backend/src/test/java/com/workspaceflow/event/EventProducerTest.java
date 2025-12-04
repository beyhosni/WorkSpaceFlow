package com.workspaceflow.event;

import com.workspaceflow.config.KafkaTopicConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for EventProducer
 */
@ExtendWith(MockitoExtension.class)
class EventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EventProducer eventProducer;

    @Test
    void publishWorkflowEvent_ShouldSendEventToKafka() {
        // Given
        String type = WorkflowEvent.INSTANCE_STARTED;
        String workflowId = "workflow-123";
        String instanceId = "instance-456";
        Map<String, Object> payload = Map.of("key", "value");

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

        // When
        eventProducer.publishWorkflowEvent(type, workflowId, instanceId, payload);

        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<WorkflowEvent> eventCaptor = ArgumentCaptor.forClass(WorkflowEvent.class);

        verify(kafkaTemplate).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo(KafkaTopicConfig.WORKFLOW_EVENTS);
        assertThat(keyCaptor.getValue()).isEqualTo(instanceId);

        WorkflowEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getType()).isEqualTo(type);
        assertThat(capturedEvent.getWorkflowId()).isEqualTo(workflowId);
        assertThat(capturedEvent.getInstanceId()).isEqualTo(instanceId);
        assertThat(capturedEvent.getPayload()).isEqualTo(payload);
        assertThat(capturedEvent.getTimestamp()).isNotNull();
    }

    @Test
    void publishTaskEvent_ShouldSendEventToKafka() {
        // Given
        String type = TaskEvent.TASK_ASSIGNED;
        String taskId = "task-123";
        String assignee = "john.doe";
        String instanceId = "instance-456";
        Map<String, Object> payload = Map.of("taskName", "Test Task");

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

        // When
        eventProducer.publishTaskEvent(type, taskId, assignee, instanceId, payload);

        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TaskEvent> eventCaptor = ArgumentCaptor.forClass(TaskEvent.class);

        verify(kafkaTemplate).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo(KafkaTopicConfig.TASK_EVENTS);
        assertThat(keyCaptor.getValue()).isEqualTo(taskId);

        TaskEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getType()).isEqualTo(type);
        assertThat(capturedEvent.getTaskId()).isEqualTo(taskId);
        assertThat(capturedEvent.getAssignee()).isEqualTo(assignee);
        assertThat(capturedEvent.getInstanceId()).isEqualTo(instanceId);
        assertThat(capturedEvent.getPayload()).isEqualTo(payload);
        assertThat(capturedEvent.getTimestamp()).isNotNull();
    }

    @Test
    void publishAuditEvent_ShouldSendEventToKafka() {
        // Given
        String eventType = "WORKFLOW_CREATED";
        String entityType = "WORKFLOW";
        String entityId = "workflow-123";
        String userId = "admin";
        Map<String, Object> payload = Map.of("name", "Test Workflow");

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

        // When
        eventProducer.publishAuditEvent(eventType, entityType, entityId, userId, payload);

        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> eventCaptor = ArgumentCaptor.forClass(Map.class);

        verify(kafkaTemplate).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo(KafkaTopicConfig.AUDIT_EVENTS);
        assertThat(keyCaptor.getValue()).isEqualTo(entityId);

        Map<String, Object> capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.get("eventType")).isEqualTo(eventType);
        assertThat(capturedEvent.get("entityType")).isEqualTo(entityType);
        assertThat(capturedEvent.get("entityId")).isEqualTo(entityId);
        assertThat(capturedEvent.get("userId")).isEqualTo(userId);
        assertThat(capturedEvent.get("timestamp")).isNotNull();
    }

    @Test
    void publishAuditEvent_ShouldUseSystemUser_WhenUserIdIsNull() {
        // Given
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

        // When
        eventProducer.publishAuditEvent("EVENT", "ENTITY", "id", null, null);

        // Then
        ArgumentCaptor<Map> eventCaptor = ArgumentCaptor.forClass(Map.class);
        verify(kafkaTemplate).send(anyString(), anyString(), eventCaptor.capture());

        Map<String, Object> capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.get("userId")).isEqualTo("system");
    }
}
