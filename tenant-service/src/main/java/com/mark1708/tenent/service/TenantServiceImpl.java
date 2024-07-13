package com.mark1708.tenent.service;

import com.mark1708.tenent.domain.Tenant;
import com.mark1708.tenent.dto.TenantDto;
import com.mark1708.tenent.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    public Page<TenantDto> getAll(Pageable pageable) {
        return tenantRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    public TenantDto getById(UUID id) {
        return tenantRepository.findById(id)
                .map(this::toDto)
                .orElseThrow();
    }

    private TenantDto toDto(Tenant tenant) {
        return new TenantDto(
                tenant.getId(),
                tenant.getSlug(),
                tenant.getName()
        );
    }
}
