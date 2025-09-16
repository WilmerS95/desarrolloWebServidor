package com.solutec.auth_service.repository;

import com.solutec.auth_service.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop5ByEntityTypeAndEntityIDAndActionOrderByChangeDateDesc(
            String entityType, Long entityID, String action
    );
}
