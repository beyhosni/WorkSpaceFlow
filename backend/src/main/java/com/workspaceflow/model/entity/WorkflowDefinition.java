package com.workspaceflow.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Workflow Definition Entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workflows")
public class WorkflowDefinition {

    @Id
    private String id;

    private String name;

    private String description;

    private List<StepDefinition> steps;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private boolean active;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepDefinition {
        private String stepId;
        private String name;
        private String type; // HUMAN_TASK, AUTOMATED, APPROVAL
        private String assigneeRole;
        private Integer order;
    }
}
