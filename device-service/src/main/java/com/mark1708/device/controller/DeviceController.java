package com.mark1708.device.controller;

import com.mark1708.device.configuration.helper.TenantId;
import com.mark1708.device.dto.DeviceDto;
import com.mark1708.device.service.DeviceService;
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
@RequestMapping("/api/v1/device")
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public Page<DeviceDto> getAll(@TenantId String tenantId, Pageable pageable) {
        log.info("Get all device for tenant {}", tenantId);
        return deviceService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public DeviceDto get(@TenantId String tenantId, @PathVariable UUID id) {
        log.info("Get device for tenant {}", tenantId);
        return deviceService.getById(id);
    }
}
