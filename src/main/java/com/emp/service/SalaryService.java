package com.emp.service;

import com.emp.common.AuthContext;
import com.emp.model.Salary;

public interface SalaryService {
    Salary mine(AuthContext ctx);
    /** Own salary, or any salary for HR/Director. Every cross-employee view is audit-logged. */
    Salary get(AuthContext ctx, long employeeId);
    Salary set(AuthContext ctx, Salary salary);
}
