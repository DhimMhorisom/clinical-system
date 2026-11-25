package com.clinicalsystem.api.consultation.dto;

import com.clinicalsystem.api.consultation.ConsultationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConsultationRequestDTO {

    @NotNull(message = "The date and time of the appointment are mandatory.")
    private LocalDateTime consultationDate;

    @NotNull(message = "The doctor's ID is mandatory.")
    private Long doctorId;

    @NotNull(message = "The patient ID is mandatory.")
    private Long patientId;
}
