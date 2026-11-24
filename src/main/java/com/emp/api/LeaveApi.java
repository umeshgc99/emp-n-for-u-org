package com.emp.api;

import com.emp.common.Endpoint;
import com.emp.common.Json;
import com.emp.common.ServiceFactory;
import com.emp.model.Decision;
import com.emp.model.LeaveRequest;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

public class LeaveApi {
    @FunctionName("applyLeave")
    public HttpResponseMessage apply(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/leaves") HttpRequestMessage<Optional<String>> req) {
        return Endpoint.created(req, ctx ->
                ServiceFactory.leaves().apply(ctx, Json.read(req.getBody().orElse(""), LeaveRequest.class)));
    }

    @FunctionName("myLeaves")
    public HttpResponseMessage mine(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/me/leaves") HttpRequestMessage<Optional<String>> req) {
        return Endpoint.ok(req, ctx -> ServiceFactory.leaves().mine(ctx));
    }

    @FunctionName("pendingLeaves")
    public HttpResponseMessage pending(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/leaves/pending") HttpRequestMessage<Optional<String>> req) {
        return Endpoint.ok(req, ctx -> ServiceFactory.leaves().pending(ctx));
    }

    @FunctionName("decideLeave")
    public HttpResponseMessage decide(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/leaves/{id}/decision") HttpRequestMessage<Optional<String>> req,
            @BindingName("id") long id) {
        return Endpoint.ok(req, ctx ->
                ServiceFactory.leaves().decide(ctx, id, Json.read(req.getBody().orElse(""), Decision.class).approved()));
    }
}
