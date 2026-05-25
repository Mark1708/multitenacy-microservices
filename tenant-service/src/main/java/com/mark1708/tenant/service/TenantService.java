package com.mark1708.tenant.service;

import com.mark1708.tenant.dto.TenantDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TenantService {

    Page<TenantDto> getAll(Pageable pageable);

    TenantDto getById(UUID id);
}
