package com.emp.repository;

public interface AuditRepository {
    void log(String actorOid, String action, Long targetEmployeeId);
}
