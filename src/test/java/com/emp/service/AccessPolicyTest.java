package com.emp.service;

import com.emp.common.ApiException;
import com.emp.common.AuthContext;
import com.emp.model.Role;
import com.emp.service.security.AccessPolicy;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessPolicyTest {
    private static AuthContext as(Role r) {
        return new AuthContext("oid", "a@b.com", Set.of(r));
    }

    @Test
    void employeeCannotViewOthersSalary() {
        assertFalse(AccessPolicy.canViewAnySalary(as(Role.EMPLOYEE)));
        assertThrows(ApiException.class, () -> AccessPolicy.require(AccessPolicy.canViewAnySalary(as(Role.EMPLOYEE))));
    }

    @Test
    void hrAndDirectorCanViewAnySalaryButOnlyHrCanChangeIt() {
        assertTrue(AccessPolicy.canViewAnySalary(as(Role.HR)));
        assertTrue(AccessPolicy.canViewAnySalary(as(Role.DIRECTOR)));
        assertTrue(AccessPolicy.canManageSalary(as(Role.HR)));
        assertFalse(AccessPolicy.canManageSalary(as(Role.DIRECTOR)));
    }

    @Test
    void adminCannotSeeSalaryButCanManageHolidays() {
        assertFalse(AccessPolicy.canViewAnySalary(as(Role.ADMIN)));
        assertTrue(AccessPolicy.canManageHolidays(as(Role.ADMIN)));
    }

    @Test
    void onlyHrAndDirectorReviewRequests() {
        assertTrue(AccessPolicy.canReview(as(Role.HR)));
        assertTrue(AccessPolicy.canReview(as(Role.DIRECTOR)));
        assertFalse(AccessPolicy.canReview(as(Role.EMPLOYEE)));
        assertFalse(AccessPolicy.canReview(as(Role.ADMIN)));
    }
}
