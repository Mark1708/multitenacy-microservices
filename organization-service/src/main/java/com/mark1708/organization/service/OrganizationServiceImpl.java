package com.mark1708.organization.service;

import com.mark1708.organization.domain.Organization;
import com.mark1708.organization.dto.OrganizationDto;
import com.mark1708.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    public Page<OrganizationDto> getAll(Pageable pageable) {
        return organizationRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    @Cacheable(value = "OrganizationCache", unless="#result == null")
    public OrganizationDto getById(UUID id) {
        return organizationRepository.findById(id)
                .map(this::toDto)
                .orElseThrow();
    }

    private OrganizationDto toDto(Organization organization) {
        return new OrganizationDto(
                organization.getId(),
                organization.getName()
        );
    }
}
