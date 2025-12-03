package com.workspaceflow.repository;

import com.workspaceflow.model.entity.AuditEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for AuditEvent
 */
@Repository
public interface AuditEventRepository extends MongoRepository<AuditEvent, String> {

    List<AuditEvent> findByEntityTypeAndEntityId(String entityType, String entityId);

    List<AuditEvent> findByUserId(String userId);

    List<AuditEvent> findByEventType(String eventType);
}
