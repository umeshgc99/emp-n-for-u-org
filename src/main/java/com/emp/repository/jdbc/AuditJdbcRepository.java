package com.emp.repository.jdbc;

import com.emp.repository.AuditRepository;

public class AuditJdbcRepository implements AuditRepository {
    @Override public void log(String actorOid, String action, Long targetEmployeeId) {
        Sql.update("INSERT INTO audit_log (actor_oid, action, target_employee_id) VALUES (?,?,?)",
                actorOid, action, targetEmployeeId);
    }
}
