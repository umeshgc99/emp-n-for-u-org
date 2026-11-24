package com.emp.model;

/** Matches the Entra ID App Role values (case-insensitive). */
public enum Role {
    EMPLOYEE, HR, DIRECTOR, ADMIN;

    public static Role from(String value) {
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
