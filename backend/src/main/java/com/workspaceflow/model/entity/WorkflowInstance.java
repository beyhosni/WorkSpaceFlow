package com.workspaceflow.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Workflow Instance Entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workflow_instances")
public class WorkflowInstance {

    @Id
    private String id;

    private String workflowId;

    private String workflowName;

    private WorkflowStatus status;

    private String currentStepId;

    private Map<String, Object> variables;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private String startedBy;

    public enum WorkflowStatus {
        STARTED,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}
