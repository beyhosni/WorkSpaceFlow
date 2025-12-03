package com.workspaceflow.repository;

import com.workspaceflow.model.entity.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Task
 */
@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByWorkflowInstanceId(String workflowInstanceId);

    List<Task> findByAssignee(String assignee);

    List<Task> findByStatus(Task.TaskStatus status);

    List<Task> findByAssigneeAndStatus(String assignee, Task.TaskStatus status);
}
