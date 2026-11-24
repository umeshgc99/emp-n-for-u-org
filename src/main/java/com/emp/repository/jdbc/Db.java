package com.emp.repository.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Single place that knows how to connect. Settings come from app settings (DB_URL, DB_USER, DB_PASSWORD);
 * in Azure, reference them from Key Vault. A new connection per call is fine for short-lived Functions
 * invocations; use the PgBouncer built into Flexible Server for pooling.
 */
public final class Db {
    private Db() {}

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(env("DB_URL"), env("DB_USER"), env("DB_PASSWORD"));
    }

    private static String env(String name) {
        String v = System.getenv(name);
        if (v == null || v.isBlank()) throw new IllegalStateException("Missing setting " + name);
        return v;
    }
}
