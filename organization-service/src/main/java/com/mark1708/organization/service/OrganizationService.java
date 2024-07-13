package com.mark1708.organization.service;

import com.mark1708.organization.dto.OrganizationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrganizationService {

    Page<OrganizationDto> getAll(Pageable pageable);

    OrganizationDto getById(UUID id);
}
