package com.emp.repository;

import com.emp.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    Optional<Employee> findById(long id);
    Optional<Employee> findByOid(String oid);
    Optional<Employee> findByEmail(String email);
    /** Links an employee row created by HR to the Entra user on first login. */
    void linkOid(long employeeId, String oid);
    List<Employee> findAll();
    Employee save(Employee employee);
}
