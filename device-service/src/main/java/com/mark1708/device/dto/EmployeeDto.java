package com.mark1708.device.dto;

import java.util.UUID;

public record EmployeeDto(
        UUID id,
        String firstName,
        String secondName,
        String middleName,
        IdNameDto organization
) {
}
