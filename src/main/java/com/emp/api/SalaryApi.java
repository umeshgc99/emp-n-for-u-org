package com.emp.api;

import com.emp.common.Endpoint;
import com.emp.common.Json;
import com.emp.common.ServiceFactory;
import com.emp.model.Salary;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

public class SalaryApi {
    @FunctionName("getMySalary")
    public HttpResponseMessage mine(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/me/salary") HttpRequestMessage<Optional<String>> req) {
        return Endpoint.ok(req, ctx -> ServiceFactory.salaries().mine(ctx));
    }

    /** HR / Director only (audit-logged). */
    @FunctionName("getEmployeeSalary")
    public HttpResponseMessage get(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/employees/{id}/salary") HttpRequestMessage<Optional<String>> req,
            @BindingName("id") long id) {
        return Endpoint.ok(req, ctx -> ServiceFactory.salaries().get(ctx, id));
    }

    /** HR only. */
    @FunctionName("setEmployeeSalary")
    public HttpResponseMessage set(
            @HttpTrigger(name = "req", methods = {HttpMethod.PUT}, authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "v1/employees/{id}/salary") HttpRequestMessage<Optional<String>> req,
            @BindingName("id") long id) {
        return Endpoint.ok(req, ctx -> {
            Salary body = Json.read(req.getBody().orElse(""), Salary.class);
            return ServiceFactory.salaries().set(ctx, new Salary(id, body.base(), body.bonus(), body.effectiveFrom()));
        });
    }
}
