package com.emp.api;

import com.emp.common.Endpoint;
import com.emp.common.Json;
import com.emp.common.ServiceFactory;
import com.emp.model.Holiday;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

public class HolidayApi {
    @FunctionName("listHolidays")
    public HttpResponseMessage list(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/holidays") HttpRequestMessage<Optional<String>> req) {
        return Endpoint.ok(req, ctx -> ServiceFactory.holidays().list(ctx));
    }

    @FunctionName("addHoliday")
    public HttpResponseMessage add(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/holidays") HttpRequestMessage<Optional<String>> req) {
        return Endpoint.created(req, ctx ->
                ServiceFactory.holidays().add(ctx, Json.read(req.getBody().orElse(""), Holiday.class)));
    }
}
