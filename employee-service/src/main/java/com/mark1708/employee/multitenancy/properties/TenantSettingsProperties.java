package com.mark1708.employee.multitenancy.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("tenant-settings")
public class TenantSettingsProperties {

    private String defaultTenant;

    private Map<String, TenantProperties> tenants = new HashMap<>();
}
