package com.clinicalsystem.api.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorRequestDTO {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "CRM  is required.")
    private String crm;

    @NotBlank(message = "CRM UF  is required.")
    @Pattern(
            regexp = "AC|AL|AP|AM|BA|CE|DF|ES|GO|MA|MT|MS|MG|PA|PB|PR|PE|PI|RJ|RN|RS|RO|RR|SC|SP|SE|TO",
            message = "Invalid Brazilian state."
    )
    private String crmUf;

    @NotNull(message = "The specialty ID is mandatory.")
    private Long specialtyId;

}
