package com.emp.model;

import java.time.LocalDate;

public record LeaveRequest(Long id, Long employeeId, String type, LocalDate fromDate, LocalDate toDate,
                           int days, String reason, String status, Long reviewedBy) {
}
