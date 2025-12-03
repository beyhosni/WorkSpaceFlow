package com.workspaceflow.repository;

import com.workspaceflow.model.entity.WorkflowDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for WorkflowDefinition
 */
@Repository
public interface WorkflowDefinitionRepository extends MongoRepository<WorkflowDefinition, String> {

    List<WorkflowDefinition> findByActiveTrue();

    List<WorkflowDefinition> findByCreatedBy(String createdBy);
}
