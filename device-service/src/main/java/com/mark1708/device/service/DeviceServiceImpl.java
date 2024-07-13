package com.mark1708.device.service;

import com.mark1708.device.domain.Device;
import com.mark1708.device.dto.DeviceDto;
import com.mark1708.device.dto.EmployeeDto;
import com.mark1708.device.dto.IdNameDto;
import com.mark1708.device.feign.EmployeeFeignClient;
import com.mark1708.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final EmployeeFeignClient employeeFeignClient;

    @Override
    public Page<DeviceDto> getAll(Pageable pageable) {
        return deviceRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    public DeviceDto getById(UUID id) {
        return deviceRepository.findById(id)
                .map(this::toDto)
                .orElseThrow();
    }

    private DeviceDto toDto(Device device) {
        EmployeeDto employeeDto = employeeFeignClient.get(device.getUnitId());
        return new DeviceDto(
                device.getId(),
                device.getImei(),
                new IdNameDto(
                        employeeDto.id(),
                        employeeDto.secondName() + " " +
                        employeeDto.firstName() + " " +
                        employeeDto.middleName()
                )
        );
    }
}
