package com.mark1708.device.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.mark1708.device.configuration.context.TenantContext;
import com.mark1708.device.domain.Device;
import com.mark1708.device.repository.DeviceRepository;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class TenantColumnIsolationIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private DeviceRepository deviceRepository;

    private static final String TENANT_A = "tenant_a";
    private static final String TENANT_B = "tenant_b";

    @BeforeEach
    void cleanUp() {
        TenantContext.setCurrentTenant(TENANT_A);
        deviceRepository.deleteAll();
        TenantContext.setCurrentTenant(TENANT_B);
        deviceRepository.deleteAll();
        TenantContext.setCurrentTenant(null);
    }

    @AfterEach
    void tearDown() {
        TenantContext.setCurrentTenant(null);
    }

    @Test
    @DisplayName("Device saved by tenant_a is invisible to tenant_b")
    void deviceSavedByTenantA_isInvisibleToTenantB() {
        TenantContext.setCurrentTenant(TENANT_A);
        Device deviceA = new Device();
        deviceA.setId(UUID.randomUUID());
        deviceA.setImei("111111111111111");
        deviceA.setTenantId(TENANT_A);
        deviceRepository.save(deviceA);

        TenantContext.setCurrentTenant(TENANT_B);
        assertThat(deviceRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Each tenant sees only its own devices")
    void eachTenantSeesOnlyOwnDevices() {
        TenantContext.setCurrentTenant(TENANT_A);
        Device deviceA = new Device();
        deviceA.setId(UUID.randomUUID());
        deviceA.setImei("AAAAAAAAAAAAAAAA");
        deviceA.setTenantId(TENANT_A);
        deviceRepository.save(deviceA);

        TenantContext.setCurrentTenant(TENANT_B);
        Device deviceB = new Device();
        deviceB.setId(UUID.randomUUID());
        deviceB.setImei("BBBBBBBBBBBBBBBB");
        deviceB.setTenantId(TENANT_B);
        deviceRepository.save(deviceB);

        TenantContext.setCurrentTenant(TENANT_A);
        assertThat(deviceRepository.findAll())
                .hasSize(1)
                .allSatisfy(d -> assertThat(d.getTenantId()).isEqualTo(TENANT_A));

        TenantContext.setCurrentTenant(TENANT_B);
        assertThat(deviceRepository.findAll())
                .hasSize(1)
                .allSatisfy(d -> assertThat(d.getTenantId()).isEqualTo(TENANT_B));
    }

    @Test
    @DisplayName("Tenant_a cannot find tenant_b device by ID")
    void tenantACannotFindTenantBDeviceById() {
        TenantContext.setCurrentTenant(TENANT_B);
        Device deviceB = new Device();
        deviceB.setId(UUID.randomUUID());
        deviceB.setImei("BBBBBBBBBBBBBBBB");
        deviceB.setTenantId(TENANT_B);
        deviceRepository.save(deviceB);
        UUID deviceIdB = deviceB.getId();

        TenantContext.setCurrentTenant(TENANT_A);
        assertThat(deviceRepository.findById(deviceIdB)).isEmpty();
    }
}
