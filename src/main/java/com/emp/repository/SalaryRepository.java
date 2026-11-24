package com.emp.repository;

import com.emp.model.Salary;

import java.util.Optional;

public interface SalaryRepository {
    Optional<Salary> findByEmployee(long employeeId);
    void upsert(Salary salary);
}
