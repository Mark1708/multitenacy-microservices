package com.mark1708.device.feign;

import com.mark1708.device.dto.EmployeeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "employee-service", path = "/api/v1/employee")
public interface EmployeeFeignClient {

    @GetMapping("/{id}")
    EmployeeDto get(@PathVariable UUID id);
}
