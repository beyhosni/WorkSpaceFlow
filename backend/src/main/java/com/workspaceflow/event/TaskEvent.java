package com.workspaceflow.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Task event model for Kafka events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEvent {

    private String type;
    private String taskId;
    private String assignee;
    private String instanceId;
    private LocalDateTime timestamp;
    private Map<String, Object> payload;

    // Event types
    public static final String TASK_CREATED = "TASK_CREATED";
    public static final String TASK_ASSIGNED = "TASK_ASSIGNED";
    public static final String TASK_COMPLETED = "TASK_COMPLETED";
    public static final String TASK_REJECTED = "TASK_REJECTED";
}
