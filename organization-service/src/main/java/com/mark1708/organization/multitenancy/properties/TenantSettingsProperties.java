package com.mark1708.organization.multitenancy.properties;

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
    private Long maximumSize;
    private Integer expireAfterAccess;

    private Map<String, TenantProperties> tenants = new HashMap<>();
}
