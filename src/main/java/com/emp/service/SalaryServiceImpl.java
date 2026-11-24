package com.emp.service;

import com.emp.common.ApiException;
import com.emp.common.AuthContext;
import com.emp.model.Salary;
import com.emp.repository.AuditRepository;
import com.emp.repository.EmployeeRepository;
import com.emp.repository.SalaryRepository;
import com.emp.service.security.AccessPolicy;

public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaries;
    private final EmployeeRepository employees;
    private final EmployeeService employeeService;
    private final AuditRepository audit;

    public SalaryServiceImpl(SalaryRepository salaries, EmployeeRepository employees,
                             EmployeeService employeeService, AuditRepository audit) {
        this.salaries = salaries;
        this.employees = employees;
        this.employeeService = employeeService;
        this.audit = audit;
    }

    @Override
    public Salary mine(AuthContext ctx) {
        long id = employeeService.current(ctx).id();
        return salaries.findByEmployee(id).orElseThrow(() -> ApiException.notFound("Salary not set yet"));
    }

    @Override
    public Salary get(AuthContext ctx, long employeeId) {
        long myId = employeeService.current(ctx).id();
        if (myId != employeeId) {
            AccessPolicy.require(AccessPolicy.canViewAnySalary(ctx));
            audit.log(ctx.oid(), "VIEW_SALARY", employeeId);
        }
        return salaries.findByEmployee(employeeId).orElseThrow(() -> ApiException.notFound("Salary not set yet"));
    }

    @Override
    public Salary set(AuthContext ctx, Salary s) {
        AccessPolicy.require(AccessPolicy.canManageSalary(ctx));
        if (s.employeeId() == null || s.base() == null || s.effectiveFrom() == null) {
            throw ApiException.badRequest("employeeId, base and effectiveFrom are required");
        }
        if (s.base().signum() < 0) throw ApiException.badRequest("base must not be negative");
        employees.findById(s.employeeId()).orElseThrow(() -> ApiException.notFound("Employee not found"));
        Salary saved = new Salary(s.employeeId(), s.base(),
                s.bonus() == null ? java.math.BigDecimal.ZERO : s.bonus(), s.effectiveFrom());
        salaries.upsert(saved);
        audit.log(ctx.oid(), "SET_SALARY", s.employeeId());
        return saved;
    }
}
