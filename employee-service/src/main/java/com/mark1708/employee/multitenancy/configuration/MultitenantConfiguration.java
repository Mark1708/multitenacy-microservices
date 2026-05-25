package com.mark1708.employee.multitenancy.configuration;

import com.mark1708.employee.multitenancy.properties.TenantSettingsProperties;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Configuration
public class MultitenantConfiguration {

    @Bean
    public DataSource dataSource(TenantSettingsProperties tenantSettings) {
        Map<Object, Object> resolvedDataSources = tenantSettings.getTenants().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()
                                .getDatasource()
                                .initializeDataSourceBuilder()
                                .build()));

        AbstractRoutingDataSource dataSource = new MultitenantDataSource(resolvedDataSources.keySet());
        dataSource.setTargetDataSources(resolvedDataSources);
        // Intentionally no defaultTargetDataSource: unresolved tenant lookup must fail,
        // not silently fall back to a default tenant's database.

        dataSource.afterPropertiesSet();
        return dataSource;
    }
}
