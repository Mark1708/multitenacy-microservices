package com.mark1708.employee.service;

import com.mark1708.employee.domain.Employee;
import com.mark1708.employee.dto.EmployeeDto;
import com.mark1708.employee.dto.IdNameDto;
import com.mark1708.employee.dto.OrganizationDto;
import com.mark1708.employee.feign.OrganizationFeignClient;
import com.mark1708.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final OrganizationFeignClient organizationFeignClient;

    @Override
    public Page<EmployeeDto> getAll(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    public EmployeeDto getById(UUID id) {
        return employeeRepository.findById(id)
                .map(this::toDto)
                .orElseThrow();
    }

    private EmployeeDto toDto(Employee employee) {
        OrganizationDto organizationDto = organizationFeignClient.get(employee.getOrganizationId());
        return new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getSecondName(),
                employee.getMiddleName(),
                new IdNameDto(
                        organizationDto.id(),
                        organizationDto.name()
                )
        );
    }
}
