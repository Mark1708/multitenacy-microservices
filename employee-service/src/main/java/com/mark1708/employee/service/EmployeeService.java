package com.mark1708.employee.service;

import com.mark1708.employee.dto.EmployeeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EmployeeService {

    Page<EmployeeDto> getAll(Pageable pageable);

    EmployeeDto getById(UUID id);
}
