package com.emp.repository;

import com.emp.model.Holiday;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository {
    List<Holiday> findAll();
    List<Holiday> findBetween(LocalDate from, LocalDate to);
    Holiday save(Holiday holiday);
}
