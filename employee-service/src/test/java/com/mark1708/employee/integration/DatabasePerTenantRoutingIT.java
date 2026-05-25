package com.mark1708.employee.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mark1708.employee.domain.Employee;
import com.mark1708.employee.multitenancy.context.TenantContext;
import com.mark1708.employee.repository.EmployeeRepository;
import java.sql.Connection;
import java.sql.Statement;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
class DatabasePerTenantRoutingIT {

    @Container
    static PostgreSQLContainer<?> tenantADb = new PostgreSQLContainer<>("postgres:16").withDatabaseName("tenant_a_db");

    @Container
    static PostgreSQLContainer<?> tenantBDb = new PostgreSQLContainer<>("postgres:16").withDatabaseName("tenant_b_db");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Wire tenant_a to its own database
        registry.add("tenant-settings.default-tenant", () -> "tenant_a");
        registry.add("tenant-settings.tenants.tenant_a.datasource.url", tenantADb::getJdbcUrl);
        registry.add("tenant-settings.tenants.tenant_a.datasource.username", tenantADb::getUsername);
        registry.add("tenant-settings.tenants.tenant_a.datasource.password", tenantADb::getPassword);
        registry.add("tenant-settings.tenants.tenant_a.datasource.driver-class-name", () -> "org.postgresql.Driver");

        // Wire tenant_b to its own database
        registry.add("tenant-settings.tenants.tenant_b.datasource.url", tenantBDb::getJdbcUrl);
        registry.add("tenant-settings.tenants.tenant_b.datasource.username", tenantBDb::getUsername);
        registry.add("tenant-settings.tenants.tenant_b.datasource.password", tenantBDb::getPassword);
        registry.add("tenant-settings.tenants.tenant_b.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final String TENANT_A = "tenant_a";
    private static final String TENANT_B = "tenant_b";

    @BeforeAll
    static void createTables() throws Exception {
        createEmployeeTable(tenantADb);
        createEmployeeTable(tenantBDb);
    }

    private static void createEmployeeTable(PostgreSQLContainer<?> container) throws Exception {
        try (Connection conn = container.createConnection("")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS mm_employee (
                        id UUID NOT NULL,
                        organization_id UUID,
                        first_name VARCHAR(255) NOT NULL,
                        second_name VARCHAR(255) NOT NULL,
                        middle_name VARCHAR(255),
                        PRIMARY KEY (id)
                    )
                    """);
            }
        }
    }

    @BeforeEach
    void cleanUp() {
        TenantContext.setCurrentTenant(TENANT_A);
        employeeRepository.deleteAll();
        TenantContext.setCurrentTenant(TENANT_B);
        employeeRepository.deleteAll();
        TenantContext.setCurrentTenant(null);
    }

    @AfterEach
    void tearDown() {
        TenantContext.setCurrentTenant(null);
    }

    @Test
    @DisplayName("Employee saved in tenant_a DB is invisible to tenant_b")
    void employeeSavedInTenantA_isInvisibleToTenantB() {
        TenantContext.setCurrentTenant(TENANT_A);
        Employee empA = new Employee();
        empA.setId(UUID.randomUUID());
        empA.setFirstName("Alice");
        empA.setSecondName("Smith");
        employeeRepository.save(empA);

        TenantContext.setCurrentTenant(TENANT_B);
        assertThat(employeeRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Each tenant sees only its own employees in separate databases")
    void eachTenantSeesOnlyOwnEmployees() {
        TenantContext.setCurrentTenant(TENANT_A);
        Employee empA = new Employee();
        empA.setId(UUID.randomUUID());
        empA.setFirstName("Alice");
        empA.setSecondName("Anderson");
        employeeRepository.save(empA);

        TenantContext.setCurrentTenant(TENANT_B);
        Employee empB = new Employee();
        empB.setId(UUID.randomUUID());
        empB.setFirstName("Bob");
        empB.setSecondName("Baker");
        employeeRepository.save(empB);

        TenantContext.setCurrentTenant(TENANT_A);
        assertThat(employeeRepository.findAll())
                .hasSize(1)
                .allSatisfy(e -> assertThat(e.getFirstName()).isEqualTo("Alice"));

        TenantContext.setCurrentTenant(TENANT_B);
        assertThat(employeeRepository.findAll())
                .hasSize(1)
                .allSatisfy(e -> assertThat(e.getFirstName()).isEqualTo("Bob"));
    }

    @Test
    @DisplayName("Tenant_a cannot find tenant_b employee by ID")
    void tenantACannotFindTenantBEmployeeById() {
        TenantContext.setCurrentTenant(TENANT_B);
        Employee empB = new Employee();
        empB.setId(UUID.randomUUID());
        empB.setFirstName("Bob");
        empB.setSecondName("Baker");
        employeeRepository.save(empB);
        UUID empBId = empB.getId();

        TenantContext.setCurrentTenant(TENANT_A);
        assertThat(employeeRepository.findById(empBId)).isEmpty();
    }

    @Test
    @DisplayName("Unknown tenant cannot fall back to default tenant database")
    void unknownTenantCannotFallbackToDefaultTenantDatabase() {
        TenantContext.setCurrentTenant(TENANT_A);
        Employee empA = new Employee();
        empA.setId(UUID.randomUUID());
        empA.setFirstName("Alice");
        empA.setSecondName("Anderson");
        employeeRepository.save(empA);

        TenantContext.setCurrentTenant("unknown_tenant");
        assertThatThrownBy(() -> employeeRepository.findAll())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unknown or missing tenant");
    }
}
