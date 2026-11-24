package com.emp.service;

import com.emp.common.ApiException;
import com.emp.common.AuthContext;
import com.emp.model.Employee;
import com.emp.model.Holiday;
import com.emp.model.LeaveRequest;
import com.emp.model.Role;
import com.emp.repository.EmployeeRepository;
import com.emp.repository.HolidayRepository;
import com.emp.repository.LeaveRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Shows the payoff of the layering: the service is tested with in-memory fakes, no database. */
class LeaveServiceImplTest {
    // 2030-01-07 is a Monday
    private static final LocalDate MON = LocalDate.of(2030, 1, 7);

    private final FakeLeaves leaves = new FakeLeaves();
    private final List<Holiday> holidayList = new ArrayList<>();
    private final LeaveServiceImpl service = new LeaveServiceImpl(leaves, new FakeHolidays(holidayList),
            new EmployeeServiceImpl(new FakeEmployees()));

    private static AuthContext user(String oid, Role role) {
        return new AuthContext(oid, oid + "@x.com", Set.of(role));
    }

    @Test
    void countsOnlyWorkingDaysExcludingWeekendAndHoliday() {
        holidayList.add(new Holiday(1L, MON.plusDays(1), "Fest")); // Tuesday
        // Mon..Sun: 5 weekdays minus the Tuesday holiday = 4
        assertEquals(4, service.workingDays(MON, MON.plusDays(6)));
    }

    @Test
    void applyFailsWithoutBalance() {
        leaves.balance = 0;
        var req = new LeaveRequest(null, null, "ANNUAL", MON, MON, 0, "trip", null, null);
        assertThrows(ApiException.class, () -> service.apply(user("emp", Role.EMPLOYEE), req));
    }

    @Test
    void applyUsesTokenIdentityNotBody() {
        leaves.balance = 5;
        var req = new LeaveRequest(null, 999L, "ANNUAL", MON, MON, 0, "trip", null, null);
        LeaveRequest saved = service.apply(user("emp", Role.EMPLOYEE), req);
        assertEquals(1L, saved.employeeId());
    }

    @Test
    void employeeCannotApproveLeave() {
        assertThrows(ApiException.class, () -> service.decide(user("emp", Role.EMPLOYEE), 1, true));
    }

    @Test
    void reviewerCannotApproveOwnLeave() {
        leaves.balance = 5;
        service.apply(user("emp", Role.EMPLOYEE), new LeaveRequest(null, null, "ANNUAL", MON, MON, 0, "x", null, null));
        assertThrows(ApiException.class, () -> service.decide(user("emp", Role.HR), 1, true));
    }

    @Test
    void hrApprovalDeductsBalance() {
        leaves.balance = 5;
        service.apply(user("emp", Role.EMPLOYEE), new LeaveRequest(null, null, "ANNUAL", MON, MON, 0, "x", null, null));
        LeaveRequest done = service.decide(user("hr", Role.HR), 1, true);
        assertEquals("APPROVED", done.status());
        assertEquals(4, leaves.balance);
    }

    // ---- in-memory fakes ----
    static class FakeEmployees implements EmployeeRepository {
        final Employee emp = new Employee(1L, "emp", "emp@x.com", "Emp", "IT", "Dev", MON, null);
        final Employee hr = new Employee(2L, "hr", "hr@x.com", "Hr", "HR", "HR", MON, null);
        public Optional<Employee> findById(long id) { return Optional.empty(); }
        public Optional<Employee> findByOid(String oid) {
            return oid.equals("emp") ? Optional.of(emp) : oid.equals("hr") ? Optional.of(hr) : Optional.empty();
        }
        public Optional<Employee> findByEmail(String email) { return Optional.empty(); }
        public void linkOid(long employeeId, String oid) { }
        public List<Employee> findAll() { return List.of(emp, hr); }
        public Employee save(Employee e) { return e; }
    }

    static class FakeHolidays implements HolidayRepository {
        final List<Holiday> list;
        FakeHolidays(List<Holiday> list) { this.list = list; }
        public List<Holiday> findAll() { return list; }
        public List<Holiday> findBetween(LocalDate from, LocalDate to) { return list; }
        public Holiday save(Holiday h) { list.add(h); return h; }
    }

    static class FakeLeaves implements LeaveRepository {
        int balance;
        LeaveRequest stored;
        public LeaveRequest save(LeaveRequest r) {
            stored = new LeaveRequest(1L, r.employeeId(), r.type(), r.fromDate(), r.toDate(), r.days(), r.reason(), "PENDING", null);
            return stored;
        }
        public Optional<LeaveRequest> findById(long id) { return Optional.ofNullable(stored); }
        public List<LeaveRequest> findByEmployee(long employeeId) { return List.of(); }
        public List<LeaveRequest> findPending() { return List.of(); }
        public boolean hasOverlap(long employeeId, LocalDate from, LocalDate to) { return false; }
        public int balance(long employeeId, String type) { return balance; }
        public void deductBalance(long employeeId, String type, int days) { balance -= days; }
        public void updateStatus(long id, String status, long reviewerId) {
            stored = new LeaveRequest(stored.id(), stored.employeeId(), stored.type(), stored.fromDate(), stored.toDate(),
                    stored.days(), stored.reason(), status, reviewerId);
        }
    }
}
