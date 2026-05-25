package com.mark1708.tenant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mark1708.tenant.dto.TenantDto;
import com.mark1708.tenant.service.TenantService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class TenantControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantService tenantService;

    @Test
    void getAllReturnsTenantPage() throws Exception {
        TenantDto tenant = new TenantDto(UUID.randomUUID(), "tenant-a", "Tenant A");
        when(tenantService.getAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(tenant), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/tenant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].slug").value("tenant-a"))
                .andExpect(jsonPath("$.content[0].name").value("Tenant A"));

        verify(tenantService).getAll(any(Pageable.class));
    }

    @Test
    void getByIdReturnsTenant() throws Exception {
        UUID id = UUID.randomUUID();
        when(tenantService.getById(id)).thenReturn(new TenantDto(id, "tenant-a", "Tenant A"));

        mockMvc.perform(get("/api/v1/tenant/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.slug").value("tenant-a"));

        verify(tenantService).getById(id);
    }
}
