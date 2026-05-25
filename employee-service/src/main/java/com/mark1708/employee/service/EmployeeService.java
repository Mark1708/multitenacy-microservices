package com.mark1708.employee.service;

import com.mark1708.employee.dto.EmployeeDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    Page<EmployeeDto> getAll(Pageable pageable);

    EmployeeDto getById(UUID id);
}
