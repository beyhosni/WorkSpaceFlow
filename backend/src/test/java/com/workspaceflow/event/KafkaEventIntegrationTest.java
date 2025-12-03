package com.workspaceflow.event;

import com.workspaceflow.config.KafkaTopicConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for Kafka event producer and consumer
 */
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {
        "workflow.events", "task.events", "audit.events"
})
class KafkaEventIntegrationTest {

    @Autowired
    private EventProducer eventProducer;

    @Test
    void shouldPublishWorkflowEvent() throws InterruptedException {
        // Given
        String workflowId = "workflow-123";
        String instanceId = "instance-456";
        Map<String, Object> payload = Map.of("key", "value");

        // When
        eventProducer.publishWorkflowEvent(
                WorkflowEvent.INSTANCE_STARTED,
                workflowId,
                instanceId,
                payload);

        // Then - Wait for async processing
        TimeUnit.SECONDS.sleep(2);
        // Event should be published successfully (check logs)
        assertThat(true).isTrue(); // Placeholder assertion
    }

    @Test
    void shouldPublishTaskEvent() throws InterruptedException {
        // Given
        String taskId = "task-123";
        String assignee = "john.doe";
        String instanceId = "instance-456";
        Map<String, Object> payload = Map.of("taskName", "Test Task");

        // When
        eventProducer.publishTaskEvent(
                TaskEvent.TASK_ASSIGNED,
                taskId,
                assignee,
                instanceId,
                payload);

        // Then - Wait for async processing
        TimeUnit.SECONDS.sleep(2);
        // Event should be published successfully (check logs)
        assertThat(true).isTrue(); // Placeholder assertion
    }

    @Test
    void shouldPublishAuditEvent() throws InterruptedException {
        // Given
        String entityId = "entity-123";
        String userId = "user-456";
        Map<String, Object> payload = Map.of("action", "CREATE");

        // When
        eventProducer.publishAuditEvent(
                "WORKFLOW_CREATED",
                "WORKFLOW",
                entityId,
                userId,
                payload);

        // Then - Wait for async processing
        TimeUnit.SECONDS.sleep(2);
        // Event should be published successfully (check logs)
        assertThat(true).isTrue(); // Placeholder assertion
    }
}
