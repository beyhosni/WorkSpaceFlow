package com.workspaceflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workspaceflow.model.dto.CompleteTaskRequest;
import com.workspaceflow.model.dto.TaskResponse;
import com.workspaceflow.service.TaskService;
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
 * Unit tests for TaskController
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskResponse taskResponse;
    private CompleteTaskRequest completeRequest;

    @BeforeEach
    void setUp() {
        taskResponse = TaskResponse.builder()
                .id("task-123")
                .name("Test Task")
                .status("CREATED")
                .workflowInstanceId("instance-456")
                .build();

        completeRequest = CompleteTaskRequest.builder()
                .completedBy("john.doe")
                .data(new HashMap<>())
                .build();
    }

    @Test
    void getAllTasks_ShouldReturnTaskList() throws Exception {
        // Given
        when(taskService.getAllTasks()).thenReturn(List.of(taskResponse));

        // When & Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("task-123"))
                .andExpect(jsonPath("$[0].name").value("Test Task"));
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        // Given
        when(taskService.getTaskById("task-123")).thenReturn(taskResponse);

        // When & Then
        mockMvc.perform(get("/api/tasks/task-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("task-123"))
                .andExpect(jsonPath("$.name").value("Test Task"));
    }

    @Test
    void getTasksByAssignee_ShouldReturnTaskList() throws Exception {
        // Given
        when(taskService.getTasksByAssignee("john.doe")).thenReturn(List.of(taskResponse));

        // When & Then
        mockMvc.perform(get("/api/tasks/assignee/john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("task-123"));
    }

    @Test
    void assignTask_ShouldReturnAssignedTask() throws Exception {
        // Given
        taskResponse.setStatus("ASSIGNED");
        taskResponse.setAssignee("john.doe");
        when(taskService.assignTask(anyString(), anyString())).thenReturn(taskResponse);

        // When & Then
        mockMvc.perform(put("/api/tasks/task-123/assign")
                .param("assignee", "john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("task-123"))
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andExpect(jsonPath("$.assignee").value("john.doe"));
    }

    @Test
    void completeTask_ShouldReturnCompletedTask() throws Exception {
        // Given
        taskResponse.setStatus("COMPLETED");
        taskResponse.setCompletedBy("john.doe");
        when(taskService.completeTask(anyString(), any(CompleteTaskRequest.class)))
                .thenReturn(taskResponse);

        // When & Then
        mockMvc.perform(put("/api/tasks/task-123/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("task-123"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.completedBy").value("john.doe"));
    }

    @Test
    void completeTask_ShouldReturnBadRequest_WhenCompletedByIsEmpty() throws Exception {
        // Given
        completeRequest.setCompletedBy("");

        // When & Then
        mockMvc.perform(put("/api/tasks/task-123/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completeRequest)))
                .andExpect(status().isBadRequest());
    }
}
