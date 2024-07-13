package com.mark1708.device.service;

import com.mark1708.device.dto.DeviceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DeviceService {

    Page<DeviceDto> getAll(Pageable pageable);

    DeviceDto getById(UUID id);
}
