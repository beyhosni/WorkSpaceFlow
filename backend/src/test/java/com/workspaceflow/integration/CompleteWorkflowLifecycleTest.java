package com.workspaceflow.integration;

import com.workspaceflow.model.dto.CompleteTaskRequest;
import com.workspaceflow.model.dto.CreateWorkflowRequest;
import com.workspaceflow.model.dto.StartWorkflowRequest;
import com.workspaceflow.model.dto.TaskResponse;
import com.workspaceflow.model.dto.WorkflowInstanceResponse;
import com.workspaceflow.model.dto.WorkflowResponse;
import com.workspaceflow.repository.AuditEventRepository;
import com.workspaceflow.repository.TaskRepository;
import com.workspaceflow.repository.WorkflowDefinitionRepository;
import com.workspaceflow.repository.WorkflowInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Complete integration test for the entire workflow lifecycle
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = {
        "workflow.events", "task.events", "notification.events", "audit.events", "deadletter.events"
})
class CompleteWorkflowLifecycleTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WorkflowDefinitionRepository workflowRepository;

    @Autowired
    private WorkflowInstanceRepository instanceRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AuditEventRepository auditEventRepository;

    @BeforeEach
    void setUp() {
        workflowRepository.deleteAll();
        instanceRepository.deleteAll();
        taskRepository.deleteAll();
        auditEventRepository.deleteAll();
    }

    @Test
    void completeWorkflowLifecycle_ShouldWorkEndToEnd() throws InterruptedException {
        // Step 1: Create a workflow
        CreateWorkflowRequest createRequest = CreateWorkflowRequest.builder()
                .name("Purchase Approval Workflow")
                .description("Multi-step approval process")
                .steps(List.of(
                        CreateWorkflowRequest.StepDefinitionDto.builder()
                                .stepId("step1")
                                .name("Manager Approval")
                                .type("APPROVAL")
                                .assigneeRole("manager")
                                .order(1)
                                .build(),
                        CreateWorkflowRequest.StepDefinitionDto.builder()
                                .stepId("step2")
                                .name("Finance Review")
                                .type("HUMAN_TASK")
                                .assigneeRole("finance")
                                .order(2)
                                .build()))
                .build();

        ResponseEntity<WorkflowResponse> createResponse = restTemplate.postForEntity(
                "/api/workflows",
                createRequest,
                WorkflowResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        String workflowId = createResponse.getBody().getId();

        // Step 2: Start a workflow instance
        StartWorkflowRequest startRequest = StartWorkflowRequest.builder()
                .workflowId(workflowId)
                .startedBy("john.doe")
                .variables(Map.of("amount", 5000, "department", "IT"))
                .build();

        ResponseEntity<WorkflowInstanceResponse> startResponse = restTemplate.postForEntity(
                "/api/workflows/start",
                startRequest,
                WorkflowInstanceResponse.class);

        assertThat(startResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(startResponse.getBody()).isNotNull();
        String instanceId = startResponse.getBody().getId();
        assertThat(startResponse.getBody().getStatus()).isEqualTo("STARTED");

        // Wait for task creation
        Thread.sleep(1000);

        // Step 3: Get all tasks
        ResponseEntity<TaskResponse[]> tasksResponse = restTemplate.getForEntity(
                "/api/tasks",
                TaskResponse[].class);

        assertThat(tasksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tasksResponse.getBody()).isNotEmpty();
        String firstTaskId = tasksResponse.getBody()[0].getId();

        // Step 4: Assign the first task
        ResponseEntity<TaskResponse> assignResponse = restTemplate.exchange(
                "/api/tasks/" + firstTaskId + "/assign?assignee=manager1",
                org.springframework.http.HttpMethod.PUT,
                null,
                TaskResponse.class);

        assertThat(assignResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(assignResponse.getBody().getStatus()).isEqualTo("ASSIGNED");
        assertThat(assignResponse.getBody().getAssignee()).isEqualTo("manager1");

        // Step 5: Complete the first task
        CompleteTaskRequest completeRequest = CompleteTaskRequest.builder()
                .completedBy("manager1")
                .data(Map.of("approved", true, "comments", "Approved"))
                .build();

        ResponseEntity<TaskResponse> completeResponse = restTemplate.exchange(
                "/api/tasks/" + firstTaskId + "/complete",
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(completeRequest),
                TaskResponse.class);

        assertThat(completeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(completeResponse.getBody().getStatus()).isEqualTo("COMPLETED");

        // Step 6: Verify workflow instance status
        ResponseEntity<WorkflowInstanceResponse> instanceResponse = restTemplate.getForEntity(
                "/api/workflows/instances/" + instanceId,
                WorkflowInstanceResponse.class);

        assertThat(instanceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Instance should still be in progress as there's a second task
        assertThat(instanceResponse.getBody().getStatus()).isIn("STARTED", "IN_PROGRESS");

        // Step 7: Verify audit events were created
        await().atMost(5, SECONDS).untilAsserted(() -> {
            long auditCount = auditEventRepository.count();
            assertThat(auditCount).isGreaterThan(0);
        });

        // Step 8: Verify all workflows endpoint
        ResponseEntity<WorkflowResponse[]> allWorkflowsResponse = restTemplate.getForEntity(
                "/api/workflows",
                WorkflowResponse[].class);

        assertThat(allWorkflowsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allWorkflowsResponse.getBody()).hasSize(1);

        // Step 9: Verify all instances endpoint
        ResponseEntity<WorkflowInstanceResponse[]> allInstancesResponse = restTemplate.getForEntity(
                "/api/workflows/instances",
                WorkflowInstanceResponse[].class);

        assertThat(allInstancesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allInstancesResponse.getBody()).hasSize(1);
    }

    @Test
    void workflowCompletion_ShouldCompleteWhenAllTasksAreDone() throws InterruptedException {
        // Create a simple workflow with one task
        CreateWorkflowRequest createRequest = CreateWorkflowRequest.builder()
                .name("Simple Workflow")
                .description("Single task workflow")
                .steps(List.of(
                        CreateWorkflowRequest.StepDefinitionDto.builder()
                                .stepId("step1")
                                .name("Single Task")
                                .type("HUMAN_TASK")
                                .order(1)
                                .build()))
                .build();

        ResponseEntity<WorkflowResponse> createResponse = restTemplate.postForEntity(
                "/api/workflows",
                createRequest,
                WorkflowResponse.class);

        String workflowId = createResponse.getBody().getId();

        // Start instance
        StartWorkflowRequest startRequest = StartWorkflowRequest.builder()
                .workflowId(workflowId)
                .startedBy("test-user")
                .variables(new HashMap<>())
                .build();

        ResponseEntity<WorkflowInstanceResponse> startResponse = restTemplate.postForEntity(
                "/api/workflows/start",
                startRequest,
                WorkflowInstanceResponse.class);

        String instanceId = startResponse.getBody().getId();

        Thread.sleep(1000);

        // Get and complete the task
        ResponseEntity<TaskResponse[]> tasksResponse = restTemplate.getForEntity(
                "/api/tasks",
                TaskResponse[].class);

        String taskId = tasksResponse.getBody()[0].getId();

        // Assign task
        restTemplate.exchange(
                "/api/tasks/" + taskId + "/assign?assignee=user1",
                org.springframework.http.HttpMethod.PUT,
                null,
                TaskResponse.class);

        // Complete task
        CompleteTaskRequest completeRequest = CompleteTaskRequest.builder()
                .completedBy("user1")
                .data(new HashMap<>())
                .build();

        restTemplate.exchange(
                "/api/tasks/" + taskId + "/complete",
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(completeRequest),
                TaskResponse.class);

        // Verify workflow is completed
        Thread.sleep(1000);

        ResponseEntity<WorkflowInstanceResponse> instanceResponse = restTemplate.getForEntity(
                "/api/workflows/instances/" + instanceId,
                WorkflowInstanceResponse.class);

        assertThat(instanceResponse.getBody().getStatus()).isEqualTo("COMPLETED");
        assertThat(instanceResponse.getBody().getCompletedAt()).isNotNull();
    }
}
