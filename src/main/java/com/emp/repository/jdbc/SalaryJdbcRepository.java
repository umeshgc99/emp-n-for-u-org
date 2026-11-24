package com.emp.repository.jdbc;

import com.emp.model.Salary;
import com.emp.repository.SalaryRepository;

import java.util.Optional;

public class SalaryJdbcRepository implements SalaryRepository {
    @Override public Optional<Salary> findByEmployee(long employeeId) {
        return Sql.queryOne("SELECT employee_id, base, bonus, effective_from FROM salaries WHERE employee_id = ?",
                rs -> new Salary(rs.getLong(1), rs.getBigDecimal(2), rs.getBigDecimal(3), rs.getDate(4).toLocalDate()),
                employeeId);
    }

    @Override public void upsert(Salary s) {
        Sql.update("INSERT INTO salaries (employee_id, base, bonus, effective_from) VALUES (?,?,?,?) "
                        + "ON CONFLICT (employee_id) DO UPDATE SET base = EXCLUDED.base, bonus = EXCLUDED.bonus, "
                        + "effective_from = EXCLUDED.effective_from",
                s.employeeId(), s.base(), s.bonus(), s.effectiveFrom());
    }
}
