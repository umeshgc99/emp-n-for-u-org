package com.emp.common;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

import java.util.Optional;
import java.util.function.Function;

/**
 * Shared plumbing for every API function: authenticate -> run action -> JSON response / error mapping.
 * Keeps the api package thin.
 */
public final class Endpoint {
    private Endpoint() {}

    public static HttpResponseMessage ok(HttpRequestMessage<Optional<String>> req, Function<AuthContext, Object> action) {
        return run(req, HttpStatus.OK, action);
    }

    public static HttpResponseMessage created(HttpRequestMessage<Optional<String>> req, Function<AuthContext, Object> action) {
        return run(req, HttpStatus.CREATED, action);
    }

    private static HttpResponseMessage run(HttpRequestMessage<Optional<String>> req, HttpStatus success,
                                           Function<AuthContext, Object> action) {
        try {
            AuthContext ctx = ServiceFactory.tokens().authenticate(req.getHeaders().get("authorization"));
            Object result = action.apply(ctx);
            return json(req, success, Json.write(result));
        } catch (ApiException e) {
            return json(req, HttpStatus.valueOf(e.status()), Json.write(new Error(e.getMessage())));
        } catch (Exception e) {
            return json(req, HttpStatus.INTERNAL_SERVER_ERROR, Json.write(new Error("Unexpected error")));
        }
    }

    private static HttpResponseMessage json(HttpRequestMessage<?> req, HttpStatus status, String body) {
        return req.createResponseBuilder(status).header("Content-Type", "application/json").body(body).build();
    }

    private record Error(String error) {}
}
