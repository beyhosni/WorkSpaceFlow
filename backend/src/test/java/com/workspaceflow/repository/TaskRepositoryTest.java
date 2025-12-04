package com.workspaceflow.repository;

import com.workspaceflow.model.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for TaskRepository
 */
@DataMongoTest
@Testcontainers
class TaskRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void findByWorkflowInstanceId_ShouldReturnTasksForInstance() {
        // Given
        Task task1 = Task.builder()
                .workflowInstanceId("instance-1")
                .stepId("step1")
                .name("Task 1")
                .status(Task.TaskStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        Task task2 = Task.builder()
                .workflowInstanceId("instance-2")
                .stepId("step1")
                .name("Task 2")
                .status(Task.TaskStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(task1);
        repository.save(task2);

        // When
        List<Task> result = repository.findByWorkflowInstanceId("instance-1");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getWorkflowInstanceId()).isEqualTo("instance-1");
    }

    @Test
    void findByAssignee_ShouldReturnTasksForAssignee() {
        // Given
        Task task1 = Task.builder()
                .workflowInstanceId("instance-1")
                .stepId("step1")
                .name("Task 1")
                .status(Task.TaskStatus.ASSIGNED)
                .assignee("john.doe")
                .createdAt(LocalDateTime.now())
                .build();

        Task task2 = Task.builder()
                .workflowInstanceId("instance-1")
                .stepId("step2")
                .name("Task 2")
                .status(Task.TaskStatus.ASSIGNED)
                .assignee("jane.smith")
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(task1);
        repository.save(task2);

        // When
        List<Task> result = repository.findByAssignee("john.doe");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAssignee()).isEqualTo("john.doe");
    }

    @Test
    void findByStatus_ShouldReturnTasksWithStatus() {
        // Given
        Task task1 = Task.builder()
                .workflowInstanceId("instance-1")
                .stepId("step1")
                .name("Task 1")
                .status(Task.TaskStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        Task task2 = Task.builder()
                .workflowInstanceId("instance-1")
                .stepId("step2")
                .name("Task 2")
                .status(Task.TaskStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(task1);
        repository.save(task2);

        // When
        List<Task> result = repository.findByStatus(Task.TaskStatus.CREATED);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Task.TaskStatus.CREATED);
    }

    @Test
    void findByAssigneeAndStatus_ShouldReturnMatchingTasks() {
        // Given
        Task task1 = Task.builder()
                .workflowInstanceId("instance-1")
                .stepId("step1")
                .name("Task 1")
                .status(Task.TaskStatus.ASSIGNED)
                .assignee("john.doe")
                .createdAt(LocalDateTime.now())
                .build();

        Task task2 = Task.builder()
                .workflowInstanceId("instance-1")
                .stepId("step2")
                .name("Task 2")
                .status(Task.TaskStatus.COMPLETED)
                .assignee("john.doe")
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(task1);
        repository.save(task2);

        // When
        List<Task> result = repository.findByAssigneeAndStatus("john.doe", Task.TaskStatus.ASSIGNED);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAssignee()).isEqualTo("john.doe");
        assertThat(result.get(0).getStatus()).isEqualTo(Task.TaskStatus.ASSIGNED);
    }
}
