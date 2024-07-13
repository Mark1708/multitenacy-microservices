package com.mark1708.employee.multitenacy.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

@Getter
@Setter
public class TenantProperties {

    private DataSourceProperties datasource;
}
