package com.mark1708.organization.multitenacy.provider;


import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mark1708.organization.multitenacy.properties.TenantSettingsProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.sql.exec.ExecutionException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private final transient DataSource datasource;
    private final TenantSettingsProperties tenantSettings;
    private final Long maximumSize;
    private final Integer expireAfterAccess;

    private transient LoadingCache<String, String> tenantSchemas;

    @PostConstruct
    private void createCache() {
        Caffeine<Object, Object> tenantsCacheBuilder = Caffeine.newBuilder();
        if (maximumSize != null) {
            tenantsCacheBuilder.maximumSize(maximumSize);
        }
        if (expireAfterAccess != null) {
            tenantsCacheBuilder.expireAfterAccess(expireAfterAccess, TimeUnit.MINUTES);
        }
        tenantSchemas = tenantsCacheBuilder
                .build(new CacheLoader<String, String>() {
                    public String load(String key) {
                        return tenantSettings.getTenants().get(key).getSchema();
                    }
                });
    }

    public SchemaMultiTenantConnectionProvider(
            DataSource datasource,
            TenantSettingsProperties tenantSettings
    ) {
        this.datasource = datasource;
        this.tenantSettings = tenantSettings;
        this.maximumSize = tenantSettings.getMaximumSize();
        this.expireAfterAccess = tenantSettings.getExpireAfterAccess();
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return datasource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        log.info("Get connection for tenant {}", tenantIdentifier);
        String tenantSchema;
        try {
            tenantSchema = tenantSchemas.get(tenantIdentifier);
        } catch (ExecutionException e) {
            throw new RuntimeException("No such tenant: " + tenantIdentifier);
        }
        final Connection connection = getAnyConnection();
        connection.setSchema(tenantSchema);
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        log.info("Release connection for tenant {}", tenantIdentifier);
        connection.setSchema(null);
        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if ( MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType) ) {
            return (T) this;
        } else {
            throw new UnknownUnwrapTypeException( unwrapType );
        }
    }
}
