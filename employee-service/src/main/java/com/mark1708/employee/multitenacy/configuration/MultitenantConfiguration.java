package com.mark1708.employee.multitenacy.configuration;

import com.mark1708.employee.multitenacy.properties.TenantSettingsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class MultitenantConfiguration {

    @Bean
    public DataSource dataSource(TenantSettingsProperties tenantSettings) {
        Map<Object, Object> resolvedDataSources = tenantSettings.getTenants()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getDatasource().initializeDataSourceBuilder().build()
                ));

        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(tenantSettings.getDefaultTenant()));
        dataSource.setTargetDataSources(resolvedDataSources);

        dataSource.afterPropertiesSet();
        return dataSource;
    }

}
