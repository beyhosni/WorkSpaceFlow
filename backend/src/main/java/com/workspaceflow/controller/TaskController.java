package com.workspaceflow.controller;

import com.workspaceflow.model.dto.CompleteTaskRequest;
import com.workspaceflow.model.dto.TaskResponse;
import com.workspaceflow.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Task operations
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Get all tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/assignee/{assignee}")
    @Operation(summary = "Get tasks by assignee")
    public ResponseEntity<List<TaskResponse>> getTasksByAssignee(@PathVariable String assignee) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(assignee));
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Assign task to user")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable String id,
            @RequestParam String assignee) {
        return ResponseEntity.ok(taskService.assignTask(id, assignee));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Complete a task")
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable String id,
            @Valid @RequestBody CompleteTaskRequest request) {
        return ResponseEntity.ok(taskService.completeTask(id, request));
    }
}
