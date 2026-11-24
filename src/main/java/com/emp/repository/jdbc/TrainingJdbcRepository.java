package com.emp.repository.jdbc;

import com.emp.model.TrainingRequest;
import com.emp.repository.TrainingRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TrainingJdbcRepository implements TrainingRepository {
    private static final String COLS = "id, employee_id, title, reason, status, reviewed_by";

    private static TrainingRequest map(ResultSet rs) throws SQLException {
        long rev = rs.getLong("reviewed_by");
        Long reviewer = rs.wasNull() ? null : rev;
        return new TrainingRequest(rs.getLong("id"), rs.getLong("employee_id"), rs.getString("title"),
                rs.getString("reason"), rs.getString("status"), reviewer);
    }

    @Override public TrainingRequest save(TrainingRequest r) {
        long id = Sql.insertReturningId(
                "INSERT INTO training_requests (employee_id, title, reason, status) VALUES (?,?,?,'PENDING') RETURNING id",
                r.employeeId(), r.title(), r.reason());
        return findById(id).orElseThrow();
    }

    @Override public Optional<TrainingRequest> findById(long id) {
        return Sql.queryOne("SELECT " + COLS + " FROM training_requests WHERE id = ?", TrainingJdbcRepository::map, id);
    }

    @Override public List<TrainingRequest> findByEmployee(long employeeId) {
        return Sql.query("SELECT " + COLS + " FROM training_requests WHERE employee_id = ? ORDER BY id DESC",
                TrainingJdbcRepository::map, employeeId);
    }

    @Override public List<TrainingRequest> findPending() {
        return Sql.query("SELECT " + COLS + " FROM training_requests WHERE status = 'PENDING' ORDER BY id",
                TrainingJdbcRepository::map);
    }

    @Override public void updateStatus(long id, String status, long reviewerId) {
        Sql.update("UPDATE training_requests SET status = ?, reviewed_by = ? WHERE id = ?", status, reviewerId, id);
    }
}
