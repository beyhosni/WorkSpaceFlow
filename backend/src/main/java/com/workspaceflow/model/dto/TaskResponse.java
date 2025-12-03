package com.workspaceflow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for task response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private String id;
    private String workflowInstanceId;
    private String stepId;
    private String name;
    private String description;
    private String status;
    private String assignee;
    private String assigneeRole;
    private Map<String, Object> data;
    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
    private String completedBy;
}
