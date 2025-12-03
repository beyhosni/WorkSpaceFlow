package com.workspaceflow.service;

import com.workspaceflow.event.EventProducer;
import com.workspaceflow.event.WorkflowEvent;
import com.workspaceflow.mapper.WorkflowMapper;
import com.workspaceflow.mapper.InstanceMapper;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing workflows
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowDefinitionRepository workflowRepository;
    private final WorkflowInstanceRepository instanceRepository;
    private final TaskRepository taskRepository;
    private final WorkflowMapper workflowMapper;
    private final InstanceMapper instanceMapper;
    private final EventProducer eventProducer;

    /**
     * Create a new workflow definition
     */
    public WorkflowResponse createWorkflow(CreateWorkflowRequest request, String createdBy) {
        log.info("Creating workflow: {}", request.getName());

        WorkflowDefinition workflow = workflowMapper.toEntity(request);
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setUpdatedAt(LocalDateTime.now());
        workflow.setCreatedBy(createdBy != null ? createdBy : "system");
        workflow.setActive(true);

        WorkflowDefinition saved = workflowRepository.save(workflow);

        // Publish audit event
        eventProducer.publishAuditEvent(
                "WORKFLOW_CREATED",
                "WORKFLOW",
                saved.getId(),
                createdBy,
                Map.of("name", saved.getName()));

        return workflowMapper.toResponse(saved);
    }

    /**
     * Get all workflows
     */
    public List<WorkflowResponse> getAllWorkflows() {
        return workflowMapper.toResponseList(workflowRepository.findAll());
    }

    /**
     * Get workflow by ID
     */
    public WorkflowResponse getWorkflowById(String id) {
        WorkflowDefinition workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found: " + id));
        return workflowMapper.toResponse(workflow);
    }

    /**
     * Start a workflow instance
     */
    public WorkflowInstanceResponse startWorkflow(StartWorkflowRequest request) {
        log.info("Starting workflow instance for workflow: {}", request.getWorkflowId());

        // Get workflow definition
        WorkflowDefinition workflow = workflowRepository.findById(request.getWorkflowId())
                .orElseThrow(() -> new RuntimeException("Workflow not found: " + request.getWorkflowId()));

        // Create workflow instance
        WorkflowInstance instance = WorkflowInstance.builder()
                .workflowId(workflow.getId())
                .workflowName(workflow.getName())
                .status(WorkflowInstance.WorkflowStatus.STARTED)
                .variables(request.getVariables() != null ? request.getVariables() : new HashMap<>())
                .startedAt(LocalDateTime.now())
                .startedBy(request.getStartedBy() != null ? request.getStartedBy() : "system")
                .build();

        WorkflowInstance savedInstance = instanceRepository.save(instance);

        // Publish workflow started event
        eventProducer.publishWorkflowEvent(
                WorkflowEvent.INSTANCE_STARTED,
                workflow.getId(),
                savedInstance.getId(),
                Map.of(
                        "workflowName", workflow.getName(),
                        "startedBy", savedInstance.getStartedBy()));

        // Create first task if workflow has steps
        if (workflow.getSteps() != null && !workflow.getSteps().isEmpty()) {
            createTaskForStep(savedInstance, workflow.getSteps().get(0));
        }

        return instanceMapper.toResponse(savedInstance);
    }

    /**
     * Get all workflow instances
     */
    public List<WorkflowInstanceResponse> getAllInstances() {
        return instanceMapper.toInstanceResponseList(instanceRepository.findAll());
    }

    /**
     * Get workflow instance by ID
     */
    public WorkflowInstanceResponse getInstanceById(String id) {
        WorkflowInstance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow instance not found: " + id));
        return instanceMapper.toResponse(instance);
    }

    /**
     * Create a task for a workflow step
     */
    private void createTaskForStep(WorkflowInstance instance, WorkflowDefinition.StepDefinition step) {
        Task task = Task.builder()
                .workflowInstanceId(instance.getId())
                .stepId(step.getStepId())
                .name(step.getName())
                .description("Task for step: " + step.getName())
                .status(Task.TaskStatus.CREATED)
                .assigneeRole(step.getAssigneeRole())
                .createdAt(LocalDateTime.now())
                .build();

        taskRepository.save(task);

        log.info("Created task {} for workflow instance {}", task.getId(), instance.getId());
    }
}
