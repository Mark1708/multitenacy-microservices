package com.mark1708.device.service;

import com.mark1708.device.dto.DeviceDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeviceService {

    Page<DeviceDto> getAll(Pageable pageable);

    DeviceDto getById(UUID id);
}
