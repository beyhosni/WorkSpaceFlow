package com.workspaceflow.service;

import com.workspaceflow.event.EventProducer;
import com.workspaceflow.event.TaskEvent;
import com.workspaceflow.mapper.InstanceMapper;
import com.workspaceflow.model.dto.CompleteTaskRequest;
import com.workspaceflow.model.dto.TaskResponse;
import com.workspaceflow.model.entity.Task;
import com.workspaceflow.model.entity.WorkflowInstance;
import com.workspaceflow.repository.TaskRepository;
import com.workspaceflow.repository.WorkflowInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service for managing tasks
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final WorkflowInstanceRepository instanceRepository;
    private final InstanceMapper instanceMapper;
    private final EventProducer eventProducer;

    /**
     * Get all tasks
     */
    public List<TaskResponse> getAllTasks() {
        return instanceMapper.toTaskResponseList(taskRepository.findAll());
    }

    /**
     * Get task by ID
     */
    public TaskResponse getTaskById(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
        return instanceMapper.toTaskResponse(task);
    }

    /**
     * Get tasks by assignee
     */
    public List<TaskResponse> getTasksByAssignee(String assignee) {
        return instanceMapper.toTaskResponseList(taskRepository.findByAssignee(assignee));
    }

    /**
     * Assign task to user
     */
    public TaskResponse assignTask(String taskId, String assignee) {
        log.info("Assigning task {} to {}", taskId, assignee);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        task.setAssignee(assignee);
        task.setStatus(Task.TaskStatus.ASSIGNED);
        task.setAssignedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);

        // Publish task assigned event
        eventProducer.publishTaskEvent(
                TaskEvent.TASK_ASSIGNED,
                savedTask.getId(),
                assignee,
                savedTask.getWorkflowInstanceId(),
                Map.of("taskName", savedTask.getName()));

        return instanceMapper.toTaskResponse(savedTask);
    }

    /**
     * Complete a task
     */
    public TaskResponse completeTask(String taskId, CompleteTaskRequest request) {
        log.info("Completing task {} by {}", taskId, request.getCompletedBy());

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        task.setStatus(Task.TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setCompletedBy(request.getCompletedBy());
        if (request.getData() != null) {
            task.setData(request.getData());
        }

        Task savedTask = taskRepository.save(task);

        // Publish task completed event
        eventProducer.publishTaskEvent(
                TaskEvent.TASK_COMPLETED,
                savedTask.getId(),
                savedTask.getAssignee(),
                savedTask.getWorkflowInstanceId(),
                Map.of(
                        "taskName", savedTask.getName(),
                        "completedBy", request.getCompletedBy()));

        // Check if all tasks for this workflow instance are completed
        checkWorkflowCompletion(savedTask.getWorkflowInstanceId());

        return instanceMapper.toTaskResponse(savedTask);
    }

    /**
     * Check if workflow instance is completed
     */
    private void checkWorkflowCompletion(String instanceId) {
        List<Task> tasks = taskRepository.findByWorkflowInstanceId(instanceId);
        boolean allCompleted = tasks.stream()
                .allMatch(t -> t.getStatus() == Task.TaskStatus.COMPLETED);

        if (allCompleted && !tasks.isEmpty()) {
            WorkflowInstance instance = instanceRepository.findById(instanceId)
                    .orElseThrow(() -> new RuntimeException("Workflow instance not found: " + instanceId));

            instance.setStatus(WorkflowInstance.WorkflowStatus.COMPLETED);
            instance.setCompletedAt(LocalDateTime.now());
            instanceRepository.save(instance);

            // Publish workflow completed event
            eventProducer.publishWorkflowEvent(
                    "INSTANCE_COMPLETED",
                    instance.getWorkflowId(),
                    instance.getId(),
                    Map.of("workflowName", instance.getWorkflowName()));

            log.info("Workflow instance {} completed", instanceId);
        }
    }
}
