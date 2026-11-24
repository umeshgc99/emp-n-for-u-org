package com.emp.service.security;

import com.emp.common.ApiException;
import com.emp.common.AuthContext;
import com.emp.model.Role;

/** The single place that says which role may do what. Services call these; nothing else decides access. */
public final class AccessPolicy {
    private AccessPolicy() {}

    public static boolean canViewAnyEmployee(AuthContext c)   { return c.has(Role.HR, Role.DIRECTOR); }
    public static boolean canViewAnySalary(AuthContext c)     { return c.has(Role.HR, Role.DIRECTOR); }
    public static boolean canManageEmployees(AuthContext c)   { return c.has(Role.HR); }
    public static boolean canManageSalary(AuthContext c)      { return c.has(Role.HR); }
    public static boolean canManageHolidays(AuthContext c)    { return c.has(Role.HR, Role.ADMIN); }
    public static boolean canReview(AuthContext c)            { return c.has(Role.HR, Role.DIRECTOR); }

    public static void require(boolean allowed) {
        if (!allowed) throw ApiException.forbidden("You do not have access to this resource");
    }
}
