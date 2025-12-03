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
 * Task Entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    private String workflowInstanceId;

    private String stepId;

    private String name;

    private String description;

    private TaskStatus status;

    private String assignee;

    private String assigneeRole;

    private Map<String, Object> data;

    private LocalDateTime createdAt;

    private LocalDateTime assignedAt;

    private LocalDateTime completedAt;

    private String completedBy;

    public enum TaskStatus {
        CREATED,
        ASSIGNED,
        IN_PROGRESS,
        COMPLETED,
        REJECTED,
        CANCELLED
    }
}
