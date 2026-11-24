package com.emp.repository.jdbc;

import com.emp.model.LeaveRequest;
import com.emp.repository.LeaveRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LeaveJdbcRepository implements LeaveRepository {
    private static final String COLS = "id, employee_id, type, from_date, to_date, days, reason, status, reviewed_by";

    private static LeaveRequest map(ResultSet rs) throws SQLException {
        long rev = rs.getLong("reviewed_by");
        Long reviewer = rs.wasNull() ? null : rev;
        return new LeaveRequest(rs.getLong("id"), rs.getLong("employee_id"), rs.getString("type"),
                rs.getDate("from_date").toLocalDate(), rs.getDate("to_date").toLocalDate(),
                rs.getInt("days"), rs.getString("reason"), rs.getString("status"), reviewer);
    }

    @Override public LeaveRequest save(LeaveRequest r) {
        long id = Sql.insertReturningId(
                "INSERT INTO leave_requests (employee_id, type, from_date, to_date, days, reason, status) "
                        + "VALUES (?,?,?,?,?,?,'PENDING') RETURNING id",
                r.employeeId(), r.type(), r.fromDate(), r.toDate(), r.days(), r.reason());
        return findById(id).orElseThrow();
    }

    @Override public Optional<LeaveRequest> findById(long id) {
        return Sql.queryOne("SELECT " + COLS + " FROM leave_requests WHERE id = ?", LeaveJdbcRepository::map, id);
    }

    @Override public List<LeaveRequest> findByEmployee(long employeeId) {
        return Sql.query("SELECT " + COLS + " FROM leave_requests WHERE employee_id = ? ORDER BY from_date DESC",
                LeaveJdbcRepository::map, employeeId);
    }

    @Override public List<LeaveRequest> findPending() {
        return Sql.query("SELECT " + COLS + " FROM leave_requests WHERE status = 'PENDING' ORDER BY from_date",
                LeaveJdbcRepository::map);
    }

    @Override public boolean hasOverlap(long employeeId, LocalDate from, LocalDate to) {
        return !Sql.query("SELECT 1 FROM leave_requests WHERE employee_id = ? AND status <> 'REJECTED' "
                + "AND from_date <= ? AND to_date >= ?", rs -> 1, employeeId, to, from).isEmpty();
    }

    @Override public int balance(long employeeId, String type) {
        return Sql.queryOne("SELECT remaining FROM leave_balances WHERE employee_id = ? AND type = ?",
                rs -> rs.getInt(1), employeeId, type).orElse(0);
    }

    @Override public void deductBalance(long employeeId, String type, int days) {
        Sql.update("UPDATE leave_balances SET remaining = remaining - ? WHERE employee_id = ? AND type = ?",
                days, employeeId, type);
    }

    @Override public void updateStatus(long id, String status, long reviewerId) {
        Sql.update("UPDATE leave_requests SET status = ?, reviewed_by = ? WHERE id = ?", status, reviewerId, id);
    }
}
