package com.clinicalsystem.api.consultation.dto;

import com.clinicalsystem.api.consultation.ConsultationStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ConsultationResponseDTO {

    private Long id;
    private LocalDateTime consultationDate;
    private String doctorName;
    private String patientName;
    private ConsultationStatus status;

    public ConsultationResponseDTO(Long id, LocalDateTime consultationDate, String doctorName, String patientName, ConsultationStatus status) {
        this.id = id;
        this.consultationDate = consultationDate;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.status = status;
    }
}
