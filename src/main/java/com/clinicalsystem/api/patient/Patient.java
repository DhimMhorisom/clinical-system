package com.clinicalsystem.api.patient;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(
        name = "patient",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_patient_cpf",
                        columnNames = "cpf"
                )
        }
)
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String cpf;

    public Patient(){}
    public Patient(String name, String cpf) {
        this.name = name;
        this.cpf = cpf;
    }
}
