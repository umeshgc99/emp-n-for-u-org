package com.emp.model;

import java.time.LocalDate;

public record Employee(Long id, String entraOid, String email, String name, String department,
                       String designation, LocalDate joiningDate, Long managerId) {
}
