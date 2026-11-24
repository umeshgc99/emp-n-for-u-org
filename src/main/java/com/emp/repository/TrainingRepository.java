package com.emp.repository;

import com.emp.model.TrainingRequest;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    TrainingRequest save(TrainingRequest request);
    Optional<TrainingRequest> findById(long id);
    List<TrainingRequest> findByEmployee(long employeeId);
    List<TrainingRequest> findPending();
    void updateStatus(long id, String status, long reviewerId);
}
