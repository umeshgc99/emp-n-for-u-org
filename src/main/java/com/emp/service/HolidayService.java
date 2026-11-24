package com.emp.service;

import com.emp.common.AuthContext;
import com.emp.model.Holiday;

import java.util.List;

public interface HolidayService {
    List<Holiday> list(AuthContext ctx);
    Holiday add(AuthContext ctx, Holiday holiday);
}
