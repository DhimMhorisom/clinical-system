package com.clinicalsystem.api.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorRequestDTO {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "CRM is required.")
    @Size(max = 20, message = "CRM must have a maximum of 20 characters.")
    private String crm;

    @NotBlank(message = "CRM UF is required.")
    @Size(min = 2, max = 2, message = "CRM UF must have exactly 2 characters.")
    @Pattern(
            regexp = "(?i)AC|AL|AP|AM|BA|CE|DF|ES|GO|MA|MT|MS|MG|PA|PB|PR|PE|PI|RJ|RN|RS|RO|RR|SC|SP|SE|TO",
            message = "Invalid Brazilian state."
    )
    private String crmUf;

    @NotNull(message = "The specialty ID is mandatory.")
    private Long specialtyId;

}
