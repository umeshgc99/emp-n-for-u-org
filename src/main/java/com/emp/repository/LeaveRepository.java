package com.emp.repository;

import com.emp.model.LeaveRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRepository {
    LeaveRequest save(LeaveRequest request);
    Optional<LeaveRequest> findById(long id);
    List<LeaveRequest> findByEmployee(long employeeId);
    List<LeaveRequest> findPending();
    boolean hasOverlap(long employeeId, LocalDate from, LocalDate to);
    int balance(long employeeId, String type);
    void deductBalance(long employeeId, String type, int days);
    void updateStatus(long id, String status, long reviewerId);
}
