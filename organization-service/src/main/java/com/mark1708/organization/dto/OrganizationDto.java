package com.mark1708.organization.dto;

import java.util.UUID;

public record OrganizationDto(
        UUID id,
        String name
) {
}
