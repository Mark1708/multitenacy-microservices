package com.mark1708.tenent.dto;

import java.util.UUID;

public record TenantDto(
        UUID id,
        String slug,
        String name
) {
}
