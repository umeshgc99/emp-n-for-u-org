package com.emp.service;

import com.emp.common.ApiException;
import com.emp.common.AuthContext;
import com.emp.model.Employee;
import com.emp.repository.EmployeeRepository;
import com.emp.service.security.AccessPolicy;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employees;

    public EmployeeServiceImpl(EmployeeRepository employees) {
        this.employees = employees;
    }

    @Override
    public Employee current(AuthContext ctx) {
        var byOid = employees.findByOid(ctx.oid());
        if (byOid.isPresent()) return byOid.get();
        if (ctx.email() != null) {
            var byEmail = employees.findByEmail(ctx.email());
            if (byEmail.isPresent()) {
                employees.linkOid(byEmail.get().id(), ctx.oid());
                return byEmail.get();
            }
        }
        throw ApiException.forbidden("No employee record exists for this account. Contact HR.");
    }

    @Override
    public Employee get(AuthContext ctx, long id) {
        Employee me = current(ctx);
        if (me.id() != id) AccessPolicy.require(AccessPolicy.canViewAnyEmployee(ctx));
        return employees.findById(id).orElseThrow(() -> ApiException.notFound("Employee not found"));
    }

    @Override
    public List<Employee> list(AuthContext ctx) {
        AccessPolicy.require(AccessPolicy.canViewAnyEmployee(ctx));
        return employees.findAll();
    }

    @Override
    public Employee create(AuthContext ctx, Employee e) {
        AccessPolicy.require(AccessPolicy.canManageEmployees(ctx));
        if (e.email() == null || e.name() == null || e.joiningDate() == null) {
            throw ApiException.badRequest("email, name and joiningDate are required");
        }
        if (employees.findByEmail(e.email()).isPresent()) throw ApiException.conflict("Employee already exists");
        return employees.save(e);
    }
}
