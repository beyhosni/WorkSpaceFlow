package com.workspaceflow.mapper;

import com.workspaceflow.model.dto.TaskResponse;
import com.workspaceflow.model.dto.WorkflowInstanceResponse;
import com.workspaceflow.model.entity.Task;
import com.workspaceflow.model.entity.WorkflowInstance;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for WorkflowInstance and Task
 */
@Mapper(componentModel = "spring")
public interface InstanceMapper {

    WorkflowInstanceResponse toResponse(WorkflowInstance entity);

    List<WorkflowInstanceResponse> toInstanceResponseList(List<WorkflowInstance> entities);

    TaskResponse toTaskResponse(Task entity);

    List<TaskResponse> toTaskResponseList(List<Task> entities);
}
