package com.emp.service;

import com.emp.common.ApiException;
import com.emp.common.AuthContext;
import com.emp.model.TrainingRequest;
import com.emp.repository.TrainingRepository;
import com.emp.service.security.AccessPolicy;

import java.util.List;

public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainings;
    private final EmployeeService employeeService;

    public TrainingServiceImpl(TrainingRepository trainings, EmployeeService employeeService) {
        this.trainings = trainings;
        this.employeeService = employeeService;
    }

    @Override
    public TrainingRequest request(AuthContext ctx, TrainingRequest r) {
        if (r.title() == null || r.title().isBlank()) throw ApiException.badRequest("title is required");
        long me = employeeService.current(ctx).id();
        return trainings.save(new TrainingRequest(null, me, r.title(), r.reason(), "PENDING", null));
    }

    @Override
    public List<TrainingRequest> mine(AuthContext ctx) {
        return trainings.findByEmployee(employeeService.current(ctx).id());
    }

    @Override
    public List<TrainingRequest> pending(AuthContext ctx) {
        AccessPolicy.require(AccessPolicy.canReview(ctx));
        return trainings.findPending();
    }

    @Override
    public TrainingRequest decide(AuthContext ctx, long id, boolean approved) {
        AccessPolicy.require(AccessPolicy.canReview(ctx));
        long reviewer = employeeService.current(ctx).id();
        TrainingRequest r = trainings.findById(id).orElseThrow(() -> ApiException.notFound("Training request not found"));
        if (r.employeeId() == reviewer) throw ApiException.forbidden("You cannot review your own request");
        if (!"PENDING".equals(r.status())) throw ApiException.conflict("Request already " + r.status());
        trainings.updateStatus(id, approved ? "APPROVED" : "REJECTED", reviewer);
        return trainings.findById(id).orElseThrow();
    }
}
