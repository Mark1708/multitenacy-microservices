package com.mark1708.device.dto;

import java.util.UUID;

public record DeviceDto(
        UUID id,
        String imei,
        IdNameDto unit
) {
}
