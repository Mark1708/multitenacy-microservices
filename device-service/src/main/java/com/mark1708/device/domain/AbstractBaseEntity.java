package com.mark1708.device.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TenantId
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

}
