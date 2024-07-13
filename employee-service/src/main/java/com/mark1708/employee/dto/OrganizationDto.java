package com.mark1708.employee.dto;

import java.util.UUID;

public record OrganizationDto(
        UUID id,
        String name
) {
}
