package com.emp.repository.jdbc;

import com.emp.model.Holiday;
import com.emp.repository.HolidayRepository;

import java.time.LocalDate;
import java.util.List;

public class HolidayJdbcRepository implements HolidayRepository {
    private static final Sql.RowMapper<Holiday> MAP =
            rs -> new Holiday(rs.getLong(1), rs.getDate(2).toLocalDate(), rs.getString(3));

    @Override public List<Holiday> findAll() {
        return Sql.query("SELECT id, holiday_date, name FROM holidays ORDER BY holiday_date", MAP);
    }

    @Override public List<Holiday> findBetween(LocalDate from, LocalDate to) {
        return Sql.query("SELECT id, holiday_date, name FROM holidays WHERE holiday_date BETWEEN ? AND ?", MAP, from, to);
    }

    @Override public Holiday save(Holiday h) {
        long id = Sql.insertReturningId("INSERT INTO holidays (holiday_date, name) VALUES (?,?) RETURNING id", h.date(), h.name());
        return new Holiday(id, h.date(), h.name());
    }
}
