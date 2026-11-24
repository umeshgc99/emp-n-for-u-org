package com.emp.model;

public record TrainingRequest(Long id, Long employeeId, String title, String reason, String status,
                              Long reviewedBy) {
}
