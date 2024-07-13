package com.mark1708.employee.multitenacy.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties("tenant-settings")
public class TenantSettingsProperties {

    private String defaultTenant;

    private Map<String, TenantProperties> tenants = new HashMap<>();
}
