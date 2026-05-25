package com.mark1708.organization.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mark1708.organization.dto.OrganizationDto;
import com.mark1708.organization.service.OrganizationService;
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
class OrganizationControllerApiTest {

    private static final String TENANT_HEADER = "X-TenantID";
    private static final String TENANT_ID = "tenant_a";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationService organizationService;

    @Test
    void getAllReturnsOrganizationPage() throws Exception {
        OrganizationDto organization = new OrganizationDto(UUID.randomUUID(), "Org A");
        when(organizationService.getAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(organization), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/organization").header(TENANT_HEADER, TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Org A"));

        verify(organizationService).getAll(any(Pageable.class));
    }

    @Test
    void getByIdReturnsOrganization() throws Exception {
        UUID id = UUID.randomUUID();
        when(organizationService.getById(id)).thenReturn(new OrganizationDto(id, "Org A"));

        mockMvc.perform(get("/api/v1/organization/{id}", id).header(TENANT_HEADER, TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Org A"));

        verify(organizationService).getById(id);
    }

    @Test
    void getAllRejectsMissingTenantHeader() throws Exception {
        mockMvc.perform(get("/api/v1/organization")).andExpect(status().isBadRequest());
    }

    @Test
    void getAllRejectsBlankTenantHeader() throws Exception {
        mockMvc.perform(get("/api/v1/organization").header(TENANT_HEADER, " ")).andExpect(status().isBadRequest());
    }
}
