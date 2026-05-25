package com.mark1708.device.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mark1708.device.dto.DeviceDto;
import com.mark1708.device.dto.IdNameDto;
import com.mark1708.device.service.DeviceService;
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
class DeviceControllerApiTest {

    private static final String TENANT_HEADER = "X-TenantID";
    private static final String TENANT_ID = "tenant_a";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Test
    void getAllReturnsDevicePage() throws Exception {
        DeviceDto device =
                new DeviceDto(UUID.randomUUID(), "111111111111111", new IdNameDto(UUID.randomUUID(), "Unit A"));
        when(deviceService.getAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(device), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/device").header(TENANT_HEADER, TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].imei").value("111111111111111"))
                .andExpect(jsonPath("$.content[0].unit.name").value("Unit A"));

        verify(deviceService).getAll(any(Pageable.class));
    }

    @Test
    void getByIdReturnsDevice() throws Exception {
        UUID id = UUID.randomUUID();
        DeviceDto device = new DeviceDto(id, "111111111111111", new IdNameDto(UUID.randomUUID(), "Unit A"));
        when(deviceService.getById(id)).thenReturn(device);

        mockMvc.perform(get("/api/v1/device/{id}", id).header(TENANT_HEADER, TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.imei").value("111111111111111"));

        verify(deviceService).getById(id);
    }

    @Test
    void getAllRejectsMissingTenantHeader() throws Exception {
        mockMvc.perform(get("/api/v1/device")).andExpect(status().isBadRequest());
    }

    @Test
    void getAllRejectsBlankTenantHeader() throws Exception {
        mockMvc.perform(get("/api/v1/device").header(TENANT_HEADER, " ")).andExpect(status().isBadRequest());
    }
}
