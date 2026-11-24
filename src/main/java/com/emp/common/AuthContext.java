package com.emp.common;

import com.emp.model.Role;

import java.util.Set;

/** Who is calling. Always built from the validated token, never from the request body. */
public record AuthContext(String oid, String email, Set<Role> roles) {
    public boolean has(Role... any) {
        for (Role r : any) {
            if (roles.contains(r)) return true;
        }
        return false;
    }
}
