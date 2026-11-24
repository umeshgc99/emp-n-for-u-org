package com.emp.service;

import com.emp.common.AuthContext;
import com.emp.model.TrainingRequest;

import java.util.List;

public interface TrainingService {
    TrainingRequest request(AuthContext ctx, TrainingRequest request);
    List<TrainingRequest> mine(AuthContext ctx);
    List<TrainingRequest> pending(AuthContext ctx);
    TrainingRequest decide(AuthContext ctx, long trainingId, boolean approved);
}
