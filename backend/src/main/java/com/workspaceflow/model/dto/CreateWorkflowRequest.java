package com.workspaceflow.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating a workflow definition
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkflowRequest {

    @NotBlank(message = "Workflow name is required")
    private String name;

    private String description;

    @NotNull(message = "Steps are required")
    private List<StepDefinitionDto> steps;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepDefinitionDto {
        @NotBlank(message = "Step ID is required")
        private String stepId;

        @NotBlank(message = "Step name is required")
        private String name;

        @NotBlank(message = "Step type is required")
        private String type;

        private String assigneeRole;

        @NotNull(message = "Step order is required")
        private Integer order;
    }
}
