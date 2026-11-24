package com.emp.service;

import com.emp.common.ApiException;
import com.emp.common.AuthContext;
import com.emp.model.Holiday;
import com.emp.repository.HolidayRepository;
import com.emp.service.security.AccessPolicy;

import java.util.List;

public class HolidayServiceImpl implements HolidayService {
    private final HolidayRepository holidays;

    public HolidayServiceImpl(HolidayRepository holidays) {
        this.holidays = holidays;
    }

    @Override
    public List<Holiday> list(AuthContext ctx) {
        return holidays.findAll();
    }

    @Override
    public Holiday add(AuthContext ctx, Holiday h) {
        AccessPolicy.require(AccessPolicy.canManageHolidays(ctx));
        if (h.date() == null || h.name() == null || h.name().isBlank()) {
            throw ApiException.badRequest("date and name are required");
        }
        return holidays.save(h);
    }
}
