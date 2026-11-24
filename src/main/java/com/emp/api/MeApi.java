package com.emp.api;

import com.emp.common.Endpoint;
import com.emp.common.ServiceFactory;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Map;
import java.util.Optional;

/** GET /api/v1/me - profile + roles of the logged-in user. The UI calls this right after SSO login. */
public class MeApi {
    @FunctionName("getMe")
    public HttpResponseMessage me(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/me") HttpRequestMessage<Optional<String>> req) {
        // ANONYMOUS at the Functions level: authentication is done by our Entra token validation in Endpoint.
        return Endpoint.ok(req, ctx -> Map.of(
                "employee", ServiceFactory.employees().current(ctx),
                "roles", ctx.roles()));
    }
}
