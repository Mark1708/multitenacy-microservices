package com.mark1708.employee.feign;

import com.mark1708.employee.dto.OrganizationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "organization-service", path = "/api/v1/organization")
public interface OrganizationFeignClient {

    @GetMapping("/{id}")
    OrganizationDto get(@PathVariable UUID id);
}
