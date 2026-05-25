package com.mark1708.employee.multitenancy.configuration;

import com.mark1708.employee.multitenancy.context.TenantContext;
import java.util.Set;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MultitenantDataSource extends AbstractRoutingDataSource {

    private final Set<Object> knownTenantKeys;

    public MultitenantDataSource(Set<Object> knownTenantKeys) {
        this.knownTenantKeys = Set.copyOf(knownTenantKeys);
    }

    @Override
    protected String determineCurrentLookupKey() {
        String key = TenantContext.getCurrentTenant();
        if (key == null || key.isBlank() || !knownTenantKeys.contains(key)) {
            throw new IllegalStateException(
                    "Unknown or missing tenant: '" + key + "'. Known tenants: " + knownTenantKeys);
        }
        return key;
    }
}
