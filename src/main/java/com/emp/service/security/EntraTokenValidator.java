package com.emp.service.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.emp.common.ApiException;
import com.emp.common.AuthContext;
import com.emp.model.Role;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Validates Microsoft Entra ID v2 access tokens: signature (via the tenant's JWKS), issuer, audience, expiry.
 * Roles come from the token's "roles" claim (Entra App Roles: Employee, HR, Director, Admin).
 */
public class EntraTokenValidator implements TokenValidator {
    private final JwkProvider jwks;
    private final String issuer;
    private final String audience;

    public EntraTokenValidator(String tenantId, String audience) {
        try {
            this.jwks = new JwkProviderBuilder(
                    new URL("https://login.microsoftonline.com/" + tenantId + "/discovery/v2.0/keys"))
                    .cached(10, 24, TimeUnit.HOURS).build();
        } catch (Exception e) {
            throw new IllegalStateException("Bad Entra tenant configuration", e);
        }
        this.issuer = "https://login.microsoftonline.com/" + tenantId + "/v2.0";
        this.audience = audience;
    }

    @Override
    public AuthContext authenticate(String header) {
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            throw ApiException.unauthorized("Missing bearer token");
        }
        String token = header.substring(7).trim();
        try {
            DecodedJWT unverified = JWT.decode(token);
            Jwk jwk = jwks.get(unverified.getKeyId());
            Algorithm alg = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            DecodedJWT jwt = JWT.require(alg).withIssuer(issuer).withAudience(audience).build().verify(token);

            Set<Role> roles = EnumSet.noneOf(Role.class);
            List<String> claim = jwt.getClaim("roles").asList(String.class);
            if (claim != null) {
                for (String r : claim) {
                    Role role = Role.from(r);
                    if (role != null) roles.add(role);
                }
            }
            if (roles.isEmpty()) roles.add(Role.EMPLOYEE);

            String email = jwt.getClaim("preferred_username").asString();
            if (email == null) email = jwt.getClaim("email").asString();
            return new AuthContext(jwt.getClaim("oid").asString(), email, roles);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw ApiException.unauthorized("Invalid token");
        }
    }
}
