package com.mark1708.employee.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "mm_employee")
public class Employee {

    @Id
    private UUID id;

    private UUID organizationId;

    // Имя
    @Column(name = "first_name", nullable = false)
    private String firstName;

    // Фамилия
    @Column(name = "second_name", nullable = false)
    private String secondName;

    // Отчество
    @Column(name = "middle_name")
    private String middleName;
}
