package com.clinicalsystem.api.consultation.dto;

import com.clinicalsystem.api.consultation.ConsultationStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ConsultationResponseDTO {

    private Long id;
    private LocalDateTime consultationDate;
    private Long doctorId;
    private Long patientId;
    private ConsultationStatus status;

    public ConsultationResponseDTO(Long id, LocalDateTime consultationDate, Long doctorId, Long patientId, ConsultationStatus status) {
        this.id = id;
        this.consultationDate = consultationDate;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.status = status;
    }
}
