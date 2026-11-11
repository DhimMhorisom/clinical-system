package com.clinicalsystem.api.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
public class PatientRequestDTO {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "CPF is required.")
    @CPF(message = "Invalid CPF.")
    @Size(max = 14, message = "CPF must have a maximum of 14 characters.")
    private String cpf;


}
