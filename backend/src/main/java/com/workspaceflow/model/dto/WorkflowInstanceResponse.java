package com.workspaceflow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for workflow instance response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowInstanceResponse {

    private String id;
    private String workflowId;
    private String workflowName;
    private String status;
    private String currentStepId;
    private Map<String, Object> variables;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String startedBy;
}
