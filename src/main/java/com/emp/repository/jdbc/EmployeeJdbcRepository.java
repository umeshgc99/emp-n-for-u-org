package com.emp.repository.jdbc;

import com.emp.model.Employee;
import com.emp.repository.EmployeeRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EmployeeJdbcRepository implements EmployeeRepository {
    private static final String COLS = "id, entra_oid, email, name, department, designation, joining_date, manager_id";

    private static Employee map(ResultSet rs) throws SQLException {
        long mgr = rs.getLong("manager_id");
        Long manager = rs.wasNull() ? null : mgr;
        return new Employee(rs.getLong("id"), rs.getString("entra_oid"), rs.getString("email"),
                rs.getString("name"), rs.getString("department"), rs.getString("designation"),
                rs.getDate("joining_date").toLocalDate(), manager);
    }

    @Override public Optional<Employee> findById(long id) {
        return Sql.queryOne("SELECT " + COLS + " FROM employees WHERE id = ?", EmployeeJdbcRepository::map, id);
    }

    @Override public Optional<Employee> findByOid(String oid) {
        return Sql.queryOne("SELECT " + COLS + " FROM employees WHERE entra_oid = ?::uuid", EmployeeJdbcRepository::map, oid);
    }

    @Override public Optional<Employee> findByEmail(String email) {
        return Sql.queryOne("SELECT " + COLS + " FROM employees WHERE lower(email) = lower(?)", EmployeeJdbcRepository::map, email);
    }

    @Override public void linkOid(long employeeId, String oid) {
        Sql.update("UPDATE employees SET entra_oid = ?::uuid WHERE id = ? AND entra_oid IS NULL", oid, employeeId);
    }

    @Override public List<Employee> findAll() {
        return Sql.query("SELECT " + COLS + " FROM employees ORDER BY name", EmployeeJdbcRepository::map);
    }

    @Override public Employee save(Employee e) {
        long id = Sql.insertReturningId(
                "INSERT INTO employees (email, name, department, designation, joining_date, manager_id) "
                        + "VALUES (?,?,?,?,?,?) RETURNING id",
                e.email(), e.name(), e.department(), e.designation(), e.joiningDate(), e.managerId());
        return findById(id).orElseThrow();
    }
}
