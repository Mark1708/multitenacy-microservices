package com.mark1708.tenent.service;

import com.mark1708.tenent.dto.TenantDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TenantService {

    Page<TenantDto> getAll(Pageable pageable);

    TenantDto getById(UUID id);
}
