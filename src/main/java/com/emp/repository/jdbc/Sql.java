package com.emp.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Tiny JDBC helper so repositories stay readable. Always uses prepared statements. */
final class Sql {
    private Sql() {}

    interface RowMapper<T> { T map(ResultSet rs) throws SQLException; }

    static <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection c = Db.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> out = new ArrayList<>();
                while (rs.next()) out.add(mapper.map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("DB error", e);
        }
    }

    static <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        return query(sql, mapper, params).stream().findFirst();
    }

    static int update(String sql, Object... params) {
        try (Connection c = Db.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("DB error", e);
        }
    }

    /** Runs an INSERT ... RETURNING id and gives back the new id. */
    static long insertReturningId(String sql, Object... params) {
        return queryOne(sql, rs -> rs.getLong(1), params).orElseThrow();
    }

    private static void bind(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            if (p instanceof java.time.LocalDate d) p = java.sql.Date.valueOf(d);
            ps.setObject(i + 1, p);
        }
    }
}
