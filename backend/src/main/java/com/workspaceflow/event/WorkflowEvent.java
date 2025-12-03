package com.workspaceflow.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Base event model for Kafka events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEvent {

    private String type;
    private String workflowId;
    private String instanceId;
    private LocalDateTime timestamp;
    private Map<String, Object> payload;

    // Event types
    public static final String INSTANCE_STARTED = "INSTANCE_STARTED";
    public static final String INSTANCE_COMPLETED = "INSTANCE_COMPLETED";
    public static final String INSTANCE_FAILED = "INSTANCE_FAILED";
    public static final String STEP_STARTED = "STEP_STARTED";
    public static final String STEP_COMPLETED = "STEP_COMPLETED";
}
