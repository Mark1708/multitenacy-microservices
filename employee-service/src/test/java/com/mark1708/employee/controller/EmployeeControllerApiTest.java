package com.mark1708.employee.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mark1708.employee.dto.EmployeeDto;
import com.mark1708.employee.dto.IdNameDto;
import com.mark1708.employee.service.EmployeeService;
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
class EmployeeControllerApiTest {

    private static final String TENANT_HEADER = "X-TenantID";
    private static final String TENANT_ID = "tenant_a";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void getAllReturnsEmployeePage() throws Exception {
        UUID organizationId = UUID.randomUUID();
        EmployeeDto employee =
                new EmployeeDto(UUID.randomUUID(), "Alice", "Smith", "P", new IdNameDto(organizationId, "Org A"));
        when(employeeService.getAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(employee), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/employee").header(TENANT_HEADER, TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.content[0].organization.name").value("Org A"));

        verify(employeeService).getAll(any(Pageable.class));
    }

    @Test
    void getByIdReturnsEmployee() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeDto employee = new EmployeeDto(id, "Alice", "Smith", "P", new IdNameDto(UUID.randomUUID(), "Org A"));
        when(employeeService.getById(id)).thenReturn(employee);

        mockMvc.perform(get("/api/v1/employee/{id}", id).header(TENANT_HEADER, TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.secondName").value("Smith"));

        verify(employeeService).getById(id);
    }

    @Test
    void getAllRejectsMissingTenantHeader() throws Exception {
        mockMvc.perform(get("/api/v1/employee")).andExpect(status().isBadRequest());
    }

    @Test
    void getAllRejectsBlankTenantHeader() throws Exception {
        mockMvc.perform(get("/api/v1/employee").header(TENANT_HEADER, " ")).andExpect(status().isBadRequest());
    }
}
