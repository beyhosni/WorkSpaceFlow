package com.workspaceflow.service;

import com.workspaceflow.event.EventProducer;
import com.workspaceflow.mapper.InstanceMapper;
import com.workspaceflow.model.dto.CompleteTaskRequest;
import com.workspaceflow.model.dto.TaskResponse;
import com.workspaceflow.model.entity.Task;
import com.workspaceflow.model.entity.WorkflowInstance;
import com.workspaceflow.repository.TaskRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskService
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private WorkflowInstanceRepository instanceRepository;

    @Mock
    private InstanceMapper instanceMapper;

    @Mock
    private EventProducer eventProducer;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private TaskResponse testTaskResponse;

    @BeforeEach
    void setUp() {
        testTask = Task.builder()
                .id("task-123")
                .workflowInstanceId("instance-456")
                .stepId("step1")
                .name("Test Task")
                .description("Test Description")
                .status(Task.TaskStatus.CREATED)
                .assigneeRole("manager")
                .createdAt(LocalDateTime.now())
                .build();

        testTaskResponse = TaskResponse.builder()
                .id("task-123")
                .name("Test Task")
                .status("CREATED")
                .build();
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        // Given
        List<Task> tasks = List.of(testTask);
        List<TaskResponse> responses = List.of(testTaskResponse);
        when(taskRepository.findAll()).thenReturn(tasks);
        when(instanceMapper.toTaskResponseList(tasks)).thenReturn(responses);

        // When
        List<TaskResponse> result = taskService.getAllTasks();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("task-123");
        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenExists() {
        // Given
        when(taskRepository.findById("task-123")).thenReturn(Optional.of(testTask));
        when(instanceMapper.toTaskResponse(testTask)).thenReturn(testTaskResponse);

        // When
        TaskResponse result = taskService.getTaskById("task-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("task-123");
        verify(taskRepository).findById("task-123");
    }

    @Test
    void getTaskById_ShouldThrowException_WhenNotExists() {
        // Given
        when(taskRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.getTaskById("non-existent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void assignTask_ShouldAssignTaskToUser() {
        // Given
        when(taskRepository.findById("task-123")).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(instanceMapper.toTaskResponse(any(Task.class))).thenReturn(testTaskResponse);

        // When
        TaskResponse result = taskService.assignTask("task-123", "john.doe");

        // Then
        assertThat(result).isNotNull();
        verify(taskRepository).save(argThat(task -> task.getAssignee().equals("john.doe") &&
                task.getStatus() == Task.TaskStatus.ASSIGNED));
        verify(eventProducer).publishTaskEvent(
                anyString(),
                eq("task-123"),
                eq("john.doe"),
                eq("instance-456"),
                anyMap());
    }

    @Test
    void completeTask_ShouldCompleteTask() {
        // Given
        testTask.setAssignee("john.doe");
        testTask.setStatus(Task.TaskStatus.ASSIGNED);

        CompleteTaskRequest request = CompleteTaskRequest.builder()
                .completedBy("john.doe")
                .data(new HashMap<>())
                .build();

        when(taskRepository.findById("task-123")).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(instanceMapper.toTaskResponse(any(Task.class))).thenReturn(testTaskResponse);
        when(taskRepository.findByWorkflowInstanceId("instance-456")).thenReturn(List.of(testTask));

        // When
        TaskResponse result = taskService.completeTask("task-123", request);

        // Then
        assertThat(result).isNotNull();
        verify(taskRepository).save(argThat(task -> task.getStatus() == Task.TaskStatus.COMPLETED &&
                task.getCompletedBy().equals("john.doe")));
        verify(eventProducer).publishTaskEvent(
                anyString(),
                eq("task-123"),
                anyString(),
                eq("instance-456"),
                anyMap());
    }

    @Test
    void completeTask_ShouldCompleteWorkflow_WhenAllTasksCompleted() {
        // Given
        testTask.setStatus(Task.TaskStatus.COMPLETED);

        CompleteTaskRequest request = CompleteTaskRequest.builder()
                .completedBy("john.doe")
                .build();

        WorkflowInstance instance = WorkflowInstance.builder()
                .id("instance-456")
                .workflowId("workflow-123")
                .workflowName("Test Workflow")
                .status(WorkflowInstance.WorkflowStatus.IN_PROGRESS)
                .build();

        when(taskRepository.findById("task-123")).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(instanceMapper.toTaskResponse(any(Task.class))).thenReturn(testTaskResponse);
        when(taskRepository.findByWorkflowInstanceId("instance-456")).thenReturn(List.of(testTask));
        when(instanceRepository.findById("instance-456")).thenReturn(Optional.of(instance));

        // When
        taskService.completeTask("task-123", request);

        // Then
        verify(instanceRepository).save(argThat(inst -> inst.getStatus() == WorkflowInstance.WorkflowStatus.COMPLETED));
        verify(eventProducer).publishWorkflowEvent(
                eq("INSTANCE_COMPLETED"),
                eq("workflow-123"),
                eq("instance-456"),
                anyMap());
    }

    @Test
    void getTasksByAssignee_ShouldReturnTasksForAssignee() {
        // Given
        List<Task> tasks = List.of(testTask);
        List<TaskResponse> responses = List.of(testTaskResponse);
        when(taskRepository.findByAssignee("john.doe")).thenReturn(tasks);
        when(instanceMapper.toTaskResponseList(tasks)).thenReturn(responses);

        // When
        List<TaskResponse> result = taskService.getTasksByAssignee("john.doe");

        // Then
        assertThat(result).hasSize(1);
        verify(taskRepository).findByAssignee("john.doe");
    }
}
