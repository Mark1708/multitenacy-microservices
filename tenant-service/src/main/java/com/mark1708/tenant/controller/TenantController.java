package com.mark1708.tenant.controller;

import com.mark1708.tenant.dto.TenantDto;
import com.mark1708.tenant.service.TenantService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenant")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public Page<TenantDto> getAll(Pageable pageable) {
        return tenantService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public TenantDto get(@PathVariable UUID id) {
        return tenantService.getById(id);
    }
}
