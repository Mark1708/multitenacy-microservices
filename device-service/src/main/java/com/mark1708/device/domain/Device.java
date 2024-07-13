package com.mark1708.device.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "mm_device")
public class Device extends AbstractBaseEntity {

    @Id
    private UUID id;

    @Column(name = "imei", nullable = false)
    private String imei;

    private UUID unitId;

}
