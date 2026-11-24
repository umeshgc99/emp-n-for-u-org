package com.emp.common;

import com.emp.repository.jdbc.AuditJdbcRepository;
import com.emp.repository.jdbc.EmployeeJdbcRepository;
import com.emp.repository.jdbc.HolidayJdbcRepository;
import com.emp.repository.jdbc.LeaveJdbcRepository;
import com.emp.repository.jdbc.SalaryJdbcRepository;
import com.emp.repository.jdbc.TrainingJdbcRepository;
import com.emp.service.EmployeeService;
import com.emp.service.EmployeeServiceImpl;
import com.emp.service.HolidayService;
import com.emp.service.HolidayServiceImpl;
import com.emp.service.LeaveService;
import com.emp.service.LeaveServiceImpl;
import com.emp.service.SalaryService;
import com.emp.service.SalaryServiceImpl;
import com.emp.service.TrainingService;
import com.emp.service.TrainingServiceImpl;
import com.emp.service.security.EntraTokenValidator;
import com.emp.service.security.TokenValidator;

/**
 * The only place where implementations are chosen and wired together (a tiny hand-rolled DI).
 * To swap PostgreSQL for another store, only the repository lines here change.
 */
public final class ServiceFactory {
    private static final TokenValidator TOKENS = new EntraTokenValidator(
            System.getenv("ENTRA_TENANT_ID"), System.getenv("ENTRA_API_AUDIENCE"));

    private static final EmployeeJdbcRepository EMPLOYEES = new EmployeeJdbcRepository();
    private static final EmployeeService EMPLOYEE_SERVICE = new EmployeeServiceImpl(EMPLOYEES);
    private static final SalaryService SALARY_SERVICE = new SalaryServiceImpl(
            new SalaryJdbcRepository(), EMPLOYEES, EMPLOYEE_SERVICE, new AuditJdbcRepository());
    private static final HolidayJdbcRepository HOLIDAYS = new HolidayJdbcRepository();
    private static final HolidayService HOLIDAY_SERVICE = new HolidayServiceImpl(HOLIDAYS);
    private static final LeaveService LEAVE_SERVICE = new LeaveServiceImpl(
            new LeaveJdbcRepository(), HOLIDAYS, EMPLOYEE_SERVICE);
    private static final TrainingService TRAINING_SERVICE = new TrainingServiceImpl(
            new TrainingJdbcRepository(), EMPLOYEE_SERVICE);

    private ServiceFactory() {}

    public static TokenValidator tokens() { return TOKENS; }
    public static EmployeeService employees() { return EMPLOYEE_SERVICE; }
    public static SalaryService salaries() { return SALARY_SERVICE; }
    public static HolidayService holidays() { return HOLIDAY_SERVICE; }
    public static LeaveService leaves() { return LEAVE_SERVICE; }
    public static TrainingService trainings() { return TRAINING_SERVICE; }
}
