package com.workspaceflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workspaceflow.model.dto.CreateWorkflowRequest;
import com.workspaceflow.model.dto.StartWorkflowRequest;
import com.workspaceflow.model.dto.WorkflowInstanceResponse;
import com.workspaceflow.model.dto.WorkflowResponse;
import com.workspaceflow.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for WorkflowController
 */
@WebMvcTest(WorkflowController.class)
class WorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WorkflowService workflowService;

    private CreateWorkflowRequest createRequest;
    private WorkflowResponse workflowResponse;
    private StartWorkflowRequest startRequest;
    private WorkflowInstanceResponse instanceResponse;

    @BeforeEach
    void setUp() {
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

        workflowResponse = WorkflowResponse.builder()
                .id("workflow-123")
                .name("Test Workflow")
                .description("Test Description")
                .build();

        startRequest = StartWorkflowRequest.builder()
                .workflowId("workflow-123")
                .startedBy("john.doe")
                .variables(new HashMap<>())
                .build();

        instanceResponse = WorkflowInstanceResponse.builder()
                .id("instance-456")
                .workflowId("workflow-123")
                .status("STARTED")
                .build();
    }

    @Test
    void createWorkflow_ShouldReturnCreated() throws Exception {
        // Given
        when(workflowService.createWorkflow(any(CreateWorkflowRequest.class), anyString()))
                .thenReturn(workflowResponse);

        // When & Then
        mockMvc.perform(post("/api/workflows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
                .header("X-User-Id", "admin"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("workflow-123"))
                .andExpect(jsonPath("$.name").value("Test Workflow"));
    }

    @Test
    void createWorkflow_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
        // Given
        createRequest.setName("");

        // When & Then
        mockMvc.perform(post("/api/workflows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllWorkflows_ShouldReturnWorkflowList() throws Exception {
        // Given
        when(workflowService.getAllWorkflows()).thenReturn(List.of(workflowResponse));

        // When & Then
        mockMvc.perform(get("/api/workflows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("workflow-123"))
                .andExpect(jsonPath("$[0].name").value("Test Workflow"));
    }

    @Test
    void getWorkflowById_ShouldReturnWorkflow() throws Exception {
        // Given
        when(workflowService.getWorkflowById("workflow-123")).thenReturn(workflowResponse);

        // When & Then
        mockMvc.perform(get("/api/workflows/workflow-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("workflow-123"))
                .andExpect(jsonPath("$.name").value("Test Workflow"));
    }

    @Test
    void startWorkflow_ShouldReturnCreated() throws Exception {
        // Given
        when(workflowService.startWorkflow(any(StartWorkflowRequest.class)))
                .thenReturn(instanceResponse);

        // When & Then
        mockMvc.perform(post("/api/workflows/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(startRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("instance-456"))
                .andExpect(jsonPath("$.status").value("STARTED"));
    }

    @Test
    void getAllInstances_ShouldReturnInstanceList() throws Exception {
        // Given
        when(workflowService.getAllInstances()).thenReturn(List.of(instanceResponse));

        // When & Then
        mockMvc.perform(get("/api/workflows/instances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("instance-456"))
                .andExpect(jsonPath("$[0].status").value("STARTED"));
    }

    @Test
    void getInstanceById_ShouldReturnInstance() throws Exception {
        // Given
        when(workflowService.getInstanceById("instance-456")).thenReturn(instanceResponse);

        // When & Then
        mockMvc.perform(get("/api/workflows/instances/instance-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("instance-456"))
                .andExpect(jsonPath("$.status").value("STARTED"));
    }
}
