package com.emp.service;

import com.emp.common.AuthContext;
import com.emp.model.LeaveRequest;

import java.util.List;

public interface LeaveService {
    LeaveRequest apply(AuthContext ctx, LeaveRequest request);
    /** Track my own leave requests and their status. */
    List<LeaveRequest> mine(AuthContext ctx);
    /** Requests waiting for review (HR / Director). */
    List<LeaveRequest> pending(AuthContext ctx);
    LeaveRequest decide(AuthContext ctx, long leaveId, boolean approved);
}
