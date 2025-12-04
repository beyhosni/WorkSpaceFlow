package com.workspaceflow.service;

import com.workspaceflow.event.EventProducer;
import com.workspaceflow.mapper.InstanceMapper;
import com.workspaceflow.mapper.WorkflowMapper;
import com.workspaceflow.model.dto.CreateWorkflowRequest;
import com.workspaceflow.model.dto.StartWorkflowRequest;
import com.workspaceflow.model.dto.WorkflowInstanceResponse;
import com.workspaceflow.model.dto.WorkflowResponse;
import com.workspaceflow.model.entity.Task;
import com.workspaceflow.model.entity.WorkflowDefinition;
import com.workspaceflow.model.entity.WorkflowInstance;
import com.workspaceflow.repository.TaskRepository;
import com.workspaceflow.repository.WorkflowDefinitionRepository;
import com.workspaceflow.repository.WorkflowInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WorkflowService
 */
@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private WorkflowDefinitionRepository workflowRepository;

    @Mock
    private WorkflowInstanceRepository instanceRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private WorkflowMapper workflowMapper;

    @Mock
    private InstanceMapper instanceMapper;

    @Mock
    private EventProducer eventProducer;

    @InjectMocks
    private WorkflowService workflowService;

    private WorkflowDefinition testWorkflow;
    private WorkflowResponse testWorkflowResponse;
    private CreateWorkflowRequest createRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        testWorkflow = WorkflowDefinition.builder()
                .id("workflow-123")
                .name("Test Workflow")
                .description("Test Description")
                .steps(List.of(
                        WorkflowDefinition.StepDefinition.builder()
                                .stepId("step1")
                                .name("Step 1")
                                .type("HUMAN_TASK")
                                .assigneeRole("manager")
                                .order(1)
                                .build()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("admin")
                .active(true)
                .build();

        testWorkflowResponse = WorkflowResponse.builder()
                .id("workflow-123")
                .name("Test Workflow")
                .description("Test Description")
                .build();

        createRequest = CreateWorkflowRequest.builder()
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
    }

    @Test
    void createWorkflow_ShouldCreateAndReturnWorkflow() {
        // Given
        when(workflowMapper.toEntity(createRequest)).thenReturn(testWorkflow);
        when(workflowRepository.save(any(WorkflowDefinition.class))).thenReturn(testWorkflow);
        when(workflowMapper.toResponse(testWorkflow)).thenReturn(testWorkflowResponse);

        // When
        WorkflowResponse result = workflowService.createWorkflow(createRequest, "admin");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("workflow-123");
        assertThat(result.getName()).isEqualTo("Test Workflow");

        verify(workflowRepository).save(any(WorkflowDefinition.class));
        verify(eventProducer).publishAuditEvent(
                eq("WORKFLOW_CREATED"),
                eq("WORKFLOW"),
                eq("workflow-123"),
                eq("admin"),
                anyMap());
    }

    @Test
    void getAllWorkflows_ShouldReturnAllWorkflows() {
        // Given
        List<WorkflowDefinition> workflows = List.of(testWorkflow);
        List<WorkflowResponse> responses = List.of(testWorkflowResponse);
        when(workflowRepository.findAll()).thenReturn(workflows);
        when(workflowMapper.toResponseList(workflows)).thenReturn(responses);

        // When
        List<WorkflowResponse> result = workflowService.getAllWorkflows();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("workflow-123");
        verify(workflowRepository).findAll();
    }

    @Test
    void getWorkflowById_ShouldReturnWorkflow_WhenExists() {
        // Given
        when(workflowRepository.findById("workflow-123")).thenReturn(Optional.of(testWorkflow));
        when(workflowMapper.toResponse(testWorkflow)).thenReturn(testWorkflowResponse);

        // When
        WorkflowResponse result = workflowService.getWorkflowById("workflow-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("workflow-123");
        verify(workflowRepository).findById("workflow-123");
    }

    @Test
    void getWorkflowById_ShouldThrowException_WhenNotExists() {
        // Given
        when(workflowRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> workflowService.getWorkflowById("non-existent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Workflow not found");
    }

    @Test
    void startWorkflow_ShouldCreateInstanceAndTask() {
        // Given
        StartWorkflowRequest startRequest = StartWorkflowRequest.builder()
                .workflowId("workflow-123")
                .startedBy("john.doe")
                .variables(new HashMap<>())
                .build();

        WorkflowInstance instance = WorkflowInstance.builder()
                .id("instance-456")
                .workflowId("workflow-123")
                .workflowName("Test Workflow")
                .status(WorkflowInstance.WorkflowStatus.STARTED)
                .startedBy("john.doe")
                .startedAt(LocalDateTime.now())
                .build();

        WorkflowInstanceResponse instanceResponse = WorkflowInstanceResponse.builder()
                .id("instance-456")
                .workflowId("workflow-123")
                .status("STARTED")
                .build();

        when(workflowRepository.findById("workflow-123")).thenReturn(Optional.of(testWorkflow));
        when(instanceRepository.save(any(WorkflowInstance.class))).thenReturn(instance);
        when(instanceMapper.toResponse(any(WorkflowInstance.class))).thenReturn(instanceResponse);

        // When
        WorkflowInstanceResponse result = workflowService.startWorkflow(startRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("instance-456");
        assertThat(result.getStatus()).isEqualTo("STARTED");

        verify(instanceRepository).save(any(WorkflowInstance.class));
        verify(taskRepository).save(any(Task.class));
        verify(eventProducer).publishWorkflowEvent(
                anyString(),
                eq("workflow-123"),
                eq("instance-456"),
                anyMap());
    }

    @Test
    void startWorkflow_ShouldThrowException_WhenWorkflowNotFound() {
        // Given
        StartWorkflowRequest startRequest = StartWorkflowRequest.builder()
                .workflowId("non-existent")
                .build();

        when(workflowRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> workflowService.startWorkflow(startRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Workflow not found");
    }
}
