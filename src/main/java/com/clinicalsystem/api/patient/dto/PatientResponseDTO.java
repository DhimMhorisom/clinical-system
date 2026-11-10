package com.clinicalsystem.api.patient.dto;

import lombok.Getter;

@Getter
public class PatientResponseDTO {

    private Long id;
    private String name;
    private String cpf;

    public PatientResponseDTO(Long id, String name, String cpf) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
    }
}
