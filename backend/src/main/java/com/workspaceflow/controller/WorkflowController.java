package com.workspaceflow.controller;

import com.workspaceflow.model.dto.CreateWorkflowRequest;
import com.workspaceflow.model.dto.StartWorkflowRequest;
import com.workspaceflow.model.dto.WorkflowInstanceResponse;
import com.workspaceflow.model.dto.WorkflowResponse;
import com.workspaceflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Workflow operations
 */
@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
@Tag(name = "Workflows", description = "Workflow management endpoints")
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    @Operation(summary = "Create a new workflow definition")
    public ResponseEntity<WorkflowResponse> createWorkflow(
            @Valid @RequestBody CreateWorkflowRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        WorkflowResponse response = workflowService.createWorkflow(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all workflow definitions")
    public ResponseEntity<List<WorkflowResponse>> getAllWorkflows() {
        return ResponseEntity.ok(workflowService.getAllWorkflows());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workflow definition by ID")
    public ResponseEntity<WorkflowResponse> getWorkflowById(@PathVariable String id) {
        return ResponseEntity.ok(workflowService.getWorkflowById(id));
    }

    @PostMapping("/start")
    @Operation(summary = "Start a new workflow instance")
    public ResponseEntity<WorkflowInstanceResponse> startWorkflow(
            @Valid @RequestBody StartWorkflowRequest request) {
        WorkflowInstanceResponse response = workflowService.startWorkflow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/instances")
    @Operation(summary = "Get all workflow instances")
    public ResponseEntity<List<WorkflowInstanceResponse>> getAllInstances() {
        return ResponseEntity.ok(workflowService.getAllInstances());
    }

    @GetMapping("/instances/{id}")
    @Operation(summary = "Get workflow instance by ID")
    public ResponseEntity<WorkflowInstanceResponse> getInstanceById(@PathVariable String id) {
        return ResponseEntity.ok(workflowService.getInstanceById(id));
    }
}
