package com.workspaceflow.mapper;

import com.workspaceflow.model.dto.CreateWorkflowRequest;
import com.workspaceflow.model.dto.WorkflowResponse;
import com.workspaceflow.model.entity.WorkflowDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for WorkflowDefinition
 */
@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "active", constant = "true")
    WorkflowDefinition toEntity(CreateWorkflowRequest request);

    WorkflowResponse toResponse(WorkflowDefinition entity);

    List<WorkflowResponse> toResponseList(List<WorkflowDefinition> entities);
}
