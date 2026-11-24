package com.emp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Salary(Long employeeId, BigDecimal base, BigDecimal bonus, LocalDate effectiveFrom) {
}
