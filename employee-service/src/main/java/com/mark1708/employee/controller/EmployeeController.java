package com.mark1708.employee.controller;

import com.mark1708.employee.multitenacy.helper.TenantId;
import com.mark1708.employee.dto.EmployeeDto;
import com.mark1708.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public Page<EmployeeDto> getAll(@TenantId String tenantId, Pageable pageable) {
        log.info("Get all employees for tenant {}", tenantId);
        return employeeService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public EmployeeDto get(@TenantId String tenantId, @PathVariable UUID id) {
        log.info("Get device for tenant {}", tenantId);
        return employeeService.getById(id);
    }
}
