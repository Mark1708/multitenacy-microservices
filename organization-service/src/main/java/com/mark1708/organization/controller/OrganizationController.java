package com.mark1708.organization.controller;

import com.mark1708.organization.multitenacy.helper.annotation.TenantId;
import com.mark1708.organization.dto.OrganizationDto;
import com.mark1708.organization.service.OrganizationService;
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
@RequestMapping("/api/v1/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    public Page<OrganizationDto> getAll(@TenantId String tenantId, Pageable pageable) {
        log.info("Get all organization for tenant {}", tenantId);
        return organizationService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public OrganizationDto get(@TenantId String tenantId, @PathVariable UUID id) {
        log.info("Get organization for tenant {}", tenantId);
        return organizationService.getById(id);
    }
}
