package com.emp.service.security;

import com.emp.common.AuthContext;

public interface TokenValidator {
    /** @param authorizationHeader raw "Bearer xxx" header; throws ApiException(401) if invalid. */
    AuthContext authenticate(String authorizationHeader);
}
