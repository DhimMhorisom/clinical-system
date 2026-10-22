package com.clinicalsystem.api.doctor.dto;

import lombok.Getter;

@Getter
public class DoctorResponseDTO {

    private final Long id;
    private final String name;
    private final String crm;
    private final String crmUf;
    private final Long specialtyId;
    private final String specialty;

    public DoctorResponseDTO(Long id, String name, String crm, String crmUf, Long specialtyId, String specialty) {
        this.id = id;
        this.name = name;
        this.crm = crm;
        this.crmUf = crmUf;
        this.specialtyId = specialtyId;
        this.specialty = specialty;
    }
}
