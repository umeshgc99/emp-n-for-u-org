package com.emp.api;

import com.emp.common.Endpoint;
import com.emp.common.Json;
import com.emp.common.ServiceFactory;
import com.emp.model.Decision;
import com.emp.model.TrainingRequest;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

public class TrainingApi {
    @FunctionName("requestTraining")
    public HttpResponseMessage request(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/trainings") HttpRequestMessage<Optional<String>> req) {
        return Endpoint.created(req, ctx ->
                ServiceFactory.trainings().request(ctx, Json.read(req.getBody().orElse(""), TrainingRequest.class)));
    }

    @FunctionName("myTrainings")
    public HttpResponseMessage mine(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/me/trainings") HttpRequestMessage<Optional<String>> req) {
        return Endpoint.ok(req, ctx -> ServiceFactory.trainings().mine(ctx));
    }

    @FunctionName("pendingTrainings")
    public HttpResponseMessage pending(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/trainings/pending") HttpRequestMessage<Optional<String>> req) {
        return Endpoint.ok(req, ctx -> ServiceFactory.trainings().pending(ctx));
    }

    @FunctionName("decideTraining")
    public HttpResponseMessage decide(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/trainings/{id}/decision") HttpRequestMessage<Optional<String>> req,
            @BindingName("id") long id) {
        return Endpoint.ok(req, ctx ->
                ServiceFactory.trainings().decide(ctx, id, Json.read(req.getBody().orElse(""), Decision.class).approved()));
    }
}
