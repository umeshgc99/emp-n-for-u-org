package com.emp.service;

import com.emp.common.ApiException;
import com.emp.common.AuthContext;
import com.emp.model.Holiday;
import com.emp.model.LeaveRequest;
import com.emp.repository.HolidayRepository;
import com.emp.repository.LeaveRepository;
import com.emp.service.security.AccessPolicy;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LeaveServiceImpl implements LeaveService {
    private static final Set<String> TYPES = Set.of("ANNUAL", "SICK", "CASUAL");

    private final LeaveRepository leaves;
    private final HolidayRepository holidays;
    private final EmployeeService employeeService;

    public LeaveServiceImpl(LeaveRepository leaves, HolidayRepository holidays, EmployeeService employeeService) {
        this.leaves = leaves;
        this.holidays = holidays;
        this.employeeService = employeeService;
    }

    @Override
    public LeaveRequest apply(AuthContext ctx, LeaveRequest r) {
        long me = employeeService.current(ctx).id();
        String type = r.type() == null ? "" : r.type().toUpperCase();
        if (!TYPES.contains(type)) throw ApiException.badRequest("type must be one of " + TYPES);
        if (r.fromDate() == null || r.toDate() == null || r.toDate().isBefore(r.fromDate())) {
            throw ApiException.badRequest("Valid fromDate and toDate are required");
        }
        int days = workingDays(r.fromDate(), r.toDate());
        if (days == 0) throw ApiException.badRequest("Selected range has no working days");
        if (leaves.hasOverlap(me, r.fromDate(), r.toDate())) throw ApiException.conflict("Overlaps an existing leave");
        if (leaves.balance(me, type) < days) throw ApiException.badRequest("Not enough " + type + " leave balance");

        // employeeId always comes from the token, never from the body
        return leaves.save(new LeaveRequest(null, me, type, r.fromDate(), r.toDate(), days, r.reason(), "PENDING", null));
    }

    @Override
    public List<LeaveRequest> mine(AuthContext ctx) {
        return leaves.findByEmployee(employeeService.current(ctx).id());
    }

    @Override
    public List<LeaveRequest> pending(AuthContext ctx) {
        AccessPolicy.require(AccessPolicy.canReview(ctx));
        return leaves.findPending();
    }

    @Override
    public LeaveRequest decide(AuthContext ctx, long leaveId, boolean approved) {
        AccessPolicy.require(AccessPolicy.canReview(ctx));
        long reviewer = employeeService.current(ctx).id();
        LeaveRequest r = leaves.findById(leaveId).orElseThrow(() -> ApiException.notFound("Leave request not found"));
        if (r.employeeId() == reviewer) throw ApiException.forbidden("You cannot review your own request");
        if (!"PENDING".equals(r.status())) throw ApiException.conflict("Request already " + r.status());

        if (approved) {
            if (leaves.balance(r.employeeId(), r.type()) < r.days()) {
                throw ApiException.conflict("Employee no longer has enough balance");
            }
            leaves.deductBalance(r.employeeId(), r.type(), r.days());
        }
        leaves.updateStatus(leaveId, approved ? "APPROVED" : "REJECTED", reviewer);
        return leaves.findById(leaveId).orElseThrow();
    }

    /** Weekdays in range, excluding public holidays. */
    int workingDays(LocalDate from, LocalDate to) {
        Set<LocalDate> off = holidays.findBetween(from, to).stream().map(Holiday::date).collect(Collectors.toSet());
        int count = 0;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            boolean weekend = d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY;
            if (!weekend && !off.contains(d)) count++;
        }
        return count;
    }
}
