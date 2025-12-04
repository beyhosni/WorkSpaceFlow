package com.workspaceflow.repository;

import com.workspaceflow.model.entity.WorkflowDefinition;
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
 * Integration tests for WorkflowDefinitionRepository
 */
@DataMongoTest
@Testcontainers
class WorkflowDefinitionRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private WorkflowDefinitionRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void findByActiveTrue_ShouldReturnOnlyActiveWorkflows() {
        // Given
        WorkflowDefinition active = WorkflowDefinition.builder()
                .name("Active Workflow")
                .description("Active")
                .steps(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("admin")
                .active(true)
                .build();

        WorkflowDefinition inactive = WorkflowDefinition.builder()
                .name("Inactive Workflow")
                .description("Inactive")
                .steps(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("admin")
                .active(false)
                .build();

        repository.save(active);
        repository.save(inactive);

        // When
        List<WorkflowDefinition> result = repository.findByActiveTrue();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Active Workflow");
        assertThat(result.get(0).isActive()).isTrue();
    }

    @Test
    void findByCreatedBy_ShouldReturnWorkflowsByCreator() {
        // Given
        WorkflowDefinition workflow1 = WorkflowDefinition.builder()
                .name("Workflow 1")
                .description("Created by admin")
                .steps(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("admin")
                .active(true)
                .build();

        WorkflowDefinition workflow2 = WorkflowDefinition.builder()
                .name("Workflow 2")
                .description("Created by user")
                .steps(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("user")
                .active(true)
                .build();

        repository.save(workflow1);
        repository.save(workflow2);

        // When
        List<WorkflowDefinition> result = repository.findByCreatedBy("admin");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCreatedBy()).isEqualTo("admin");
    }

    @Test
    void save_ShouldPersistWorkflow() {
        // Given
        WorkflowDefinition workflow = WorkflowDefinition.builder()
                .name("Test Workflow")
                .description("Test Description")
                .steps(List.of(
                        WorkflowDefinition.StepDefinition.builder()
                                .stepId("step1")
                                .name("Step 1")
                                .type("HUMAN_TASK")
                                .order(1)
                                .build()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("admin")
                .active(true)
                .build();

        // When
        WorkflowDefinition saved = repository.save(workflow);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test Workflow");
        assertThat(saved.getSteps()).hasSize(1);
    }
}
