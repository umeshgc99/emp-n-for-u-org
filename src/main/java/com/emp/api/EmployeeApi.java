package com.emp.api;

import com.emp.common.Endpoint;
import com.emp.common.Json;
import com.emp.common.ServiceFactory;
import com.emp.model.Employee;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

public class EmployeeApi {
    @FunctionName("listEmployees")
    public HttpResponseMessage list(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/employees") HttpRequestMessage<Optional<String>> req) {
        return Endpoint.ok(req, ctx -> ServiceFactory.employees().list(ctx));
    }

    @FunctionName("createEmployee")
    public HttpResponseMessage create(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/employees") HttpRequestMessage<Optional<String>> req) {
        return Endpoint.created(req, ctx ->
                ServiceFactory.employees().create(ctx, Json.read(req.getBody().orElse(""), Employee.class)));
    }

    @FunctionName("getEmployee")
    public HttpResponseMessage get(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/employees/{id}") HttpRequestMessage<Optional<String>> req,
            @BindingName("id") long id) {
        return Endpoint.ok(req, ctx -> ServiceFactory.employees().get(ctx, id));
    }
}
