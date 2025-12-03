package com.workspaceflow.integration;

import com.workspaceflow.event.EventProducer;
import com.workspaceflow.event.WorkflowEvent;
import com.workspaceflow.model.dto.CreateWorkflowRequest;
import com.workspaceflow.model.dto.StartWorkflowRequest;
import com.workspaceflow.model.dto.WorkflowInstanceResponse;
import com.workspaceflow.model.dto.WorkflowResponse;
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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for Workflow API with Testcontainers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = {
        "workflow.events", "task.events", "notification.events", "audit.events", "deadletter.events"
})
class WorkflowIntegrationTest {

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

    @BeforeEach
    void setUp() {
        workflowRepository.deleteAll();
        instanceRepository.deleteAll();
    }

    @Test
    void shouldCreateWorkflow() {
        // Given
        CreateWorkflowRequest request = CreateWorkflowRequest.builder()
                .name("Test Workflow")
                .description("Test Description")
                .steps(List.of(
                        CreateWorkflowRequest.StepDefinitionDto.builder()
                                .stepId("step1")
                                .name("Step 1")
                                .type("HUMAN_TASK")
                                .assigneeRole("manager")
                                .order(1)
                                .build()))
                .build();

        // When
        ResponseEntity<WorkflowResponse> response = restTemplate.postForEntity(
                "/api/workflows",
                request,
                WorkflowResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Workflow");
        assertThat(response.getBody().getSteps()).hasSize(1);
    }

    @Test
    void shouldStartWorkflowInstance() {
        // Given - Create a workflow first
        CreateWorkflowRequest createRequest = CreateWorkflowRequest.builder()
                .name("Purchase Approval")
                .description("Approval workflow")
                .steps(List.of(
                        CreateWorkflowRequest.StepDefinitionDto.builder()
                                .stepId("step1")
                                .name("Manager Approval")
                                .type("APPROVAL")
                                .assigneeRole("manager")
                                .order(1)
                                .build()))
                .build();

        ResponseEntity<WorkflowResponse> createResponse = restTemplate.postForEntity(
                "/api/workflows",
                createRequest,
                WorkflowResponse.class);

        String workflowId = createResponse.getBody().getId();

        // When - Start an instance
        StartWorkflowRequest startRequest = StartWorkflowRequest.builder()
                .workflowId(workflowId)
                .startedBy("john.doe")
                .variables(Map.of("amount", 5000))
                .build();

        ResponseEntity<WorkflowInstanceResponse> startResponse = restTemplate.postForEntity(
                "/api/workflows/start",
                startRequest,
                WorkflowInstanceResponse.class);

        // Then
        assertThat(startResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(startResponse.getBody()).isNotNull();
        assertThat(startResponse.getBody().getWorkflowId()).isEqualTo(workflowId);
        assertThat(startResponse.getBody().getStatus()).isEqualTo("STARTED");
        assertThat(startResponse.getBody().getStartedBy()).isEqualTo("john.doe");
    }

    @Test
    void shouldGetAllWorkflows() {
        // Given - Create two workflows
        createWorkflow("Workflow 1");
        createWorkflow("Workflow 2");

        // When
        ResponseEntity<WorkflowResponse[]> response = restTemplate.getForEntity(
                "/api/workflows",
                WorkflowResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    private void createWorkflow(String name) {
        CreateWorkflowRequest request = CreateWorkflowRequest.builder()
                .name(name)
                .description("Test workflow")
                .steps(List.of(
                        CreateWorkflowRequest.StepDefinitionDto.builder()
                                .stepId("step1")
                                .name("Step 1")
                                .type("HUMAN_TASK")
                                .order(1)
                                .build()))
                .build();

        restTemplate.postForEntity("/api/workflows", request, WorkflowResponse.class);
    }
}
