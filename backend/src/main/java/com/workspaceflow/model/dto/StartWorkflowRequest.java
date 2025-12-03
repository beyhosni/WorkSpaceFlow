package com.workspaceflow.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for starting a workflow instance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartWorkflowRequest {

    @NotBlank(message = "Workflow ID is required")
    private String workflowId;

    private Map<String, Object> variables;

    private String startedBy;
}
