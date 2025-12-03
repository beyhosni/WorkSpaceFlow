package com.workspaceflow.repository;

import com.workspaceflow.model.entity.WorkflowInstance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for WorkflowInstance
 */
@Repository
public interface WorkflowInstanceRepository extends MongoRepository<WorkflowInstance, String> {

    List<WorkflowInstance> findByWorkflowId(String workflowId);

    List<WorkflowInstance> findByStatus(WorkflowInstance.WorkflowStatus status);

    List<WorkflowInstance> findByStartedBy(String startedBy);
}
