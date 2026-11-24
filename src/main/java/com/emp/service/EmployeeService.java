package com.emp.service;

import com.emp.common.AuthContext;
import com.emp.model.Employee;

import java.util.List;

public interface EmployeeService {
    /** The employee record of the logged-in user (links Entra account on first login). */
    Employee current(AuthContext ctx);
    Employee get(AuthContext ctx, long id);
    List<Employee> list(AuthContext ctx);
    Employee create(AuthContext ctx, Employee employee);
}
