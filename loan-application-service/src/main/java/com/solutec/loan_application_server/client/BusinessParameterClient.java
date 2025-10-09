package com.solutec.loan_application_server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "business-parameter-service", path = "/api/parameters")
public interface BusinessParameterClient {

    @GetMapping("/{name}/value")
    Map<String, String> getParameterValue(@PathVariable String name);
}