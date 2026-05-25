package com.mark1708.organization.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.mark1708.organization.domain.Organization;
import com.mark1708.organization.dto.OrganizationDto;
import com.mark1708.organization.multitenancy.context.TenantContext;
import com.mark1708.organization.repository.OrganizationRepository;
import com.mark1708.organization.service.OrganizationService;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import javax.sql.DataSource;
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
class SchemaIsolationIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("tenant-settings.default-tenant", () -> "tenant_a");
        registry.add("tenant-settings.tenants.tenant_a.schema", () -> "schema_a");
        registry.add("tenant-settings.tenants.tenant_b.schema", () -> "schema_b");
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationService organizationService;

    private static final String TENANT_A = "tenant_a";
    private static final String TENANT_B = "tenant_b";

    @BeforeAll
    static void createSchemas() throws SQLException {
        // Create schemas inside the container DB before Spring context fully initializes
        try (Connection conn = postgres.createConnection("")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE SCHEMA IF NOT EXISTS schema_a");
                stmt.execute("CREATE SCHEMA IF NOT EXISTS schema_b");
                // Create the mm_organization table in each schema
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS schema_a.mm_organization (
                        id UUID NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id)
                    )
                    """);
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS schema_b.mm_organization (
                        id UUID NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id)
                    )
                    """);
            }
        }
    }

    @BeforeEach
    void cleanUp() {
        TenantContext.setCurrentTenant(TENANT_A);
        organizationRepository.deleteAll();
        TenantContext.setCurrentTenant(TENANT_B);
        organizationRepository.deleteAll();
        TenantContext.setCurrentTenant(null);
    }

    @AfterEach
    void tearDown() {
        TenantContext.setCurrentTenant(null);
    }

    @Test
    @DisplayName("Organization saved in tenant_a schema is invisible to tenant_b")
    void orgSavedInTenantA_isInvisibleToTenantB() {
        TenantContext.setCurrentTenant(TENANT_A);
        Organization orgA = new Organization();
        orgA.setId(UUID.randomUUID());
        orgA.setName("Org Alpha");
        organizationRepository.save(orgA);

        TenantContext.setCurrentTenant(TENANT_B);
        assertThat(organizationRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Each tenant sees only its own organizations in separate schemas")
    void eachTenantSeesOnlyOwnOrganizations() {
        TenantContext.setCurrentTenant(TENANT_A);
        Organization orgA = new Organization();
        orgA.setId(UUID.randomUUID());
        orgA.setName("Org Alpha");
        organizationRepository.save(orgA);

        TenantContext.setCurrentTenant(TENANT_B);
        Organization orgB = new Organization();
        orgB.setId(UUID.randomUUID());
        orgB.setName("Org Beta");
        organizationRepository.save(orgB);

        TenantContext.setCurrentTenant(TENANT_A);
        assertThat(organizationRepository.findAll())
                .hasSize(1)
                .allSatisfy(o -> assertThat(o.getName()).isEqualTo("Org Alpha"));

        TenantContext.setCurrentTenant(TENANT_B);
        assertThat(organizationRepository.findAll())
                .hasSize(1)
                .allSatisfy(o -> assertThat(o.getName()).isEqualTo("Org Beta"));
    }

    @Test
    @DisplayName("Cache does not leak across tenants for same UUID")
    void cacheDoesNotLeakAcrossTenantsForSameUuid() {
        UUID sharedId = UUID.randomUUID();

        // Create organization with same UUID but different names in each tenant
        TenantContext.setCurrentTenant(TENANT_A);
        Organization orgA = new Organization();
        orgA.setId(sharedId);
        orgA.setName("Org Alpha");
        organizationRepository.save(orgA);

        TenantContext.setCurrentTenant(TENANT_B);
        Organization orgB = new Organization();
        orgB.setId(sharedId);
        orgB.setName("Org Beta");
        organizationRepository.save(orgB);

        // Warm cache for tenant A
        TenantContext.setCurrentTenant(TENANT_A);
        OrganizationDto resultA = organizationService.getById(sharedId);
        assertThat(resultA.name()).isEqualTo("Org Alpha");

        // Tenant B must get its own cached value, not tenant A's
        TenantContext.setCurrentTenant(TENANT_B);
        OrganizationDto resultB = organizationService.getById(sharedId);
        assertThat(resultB.name()).isEqualTo("Org Beta");
    }

    @Test
    @DisplayName("Tenant_a cannot find tenant_b organization by ID")
    void tenantACannotFindTenantBOrgById() {
        TenantContext.setCurrentTenant(TENANT_B);
        Organization orgB = new Organization();
        orgB.setId(UUID.randomUUID());
        orgB.setName("Org Beta");
        organizationRepository.save(orgB);
        UUID orgBId = orgB.getId();

        TenantContext.setCurrentTenant(TENANT_A);
        assertThat(organizationRepository.findById(orgBId)).isEmpty();
    }
}
