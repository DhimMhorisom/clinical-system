package com.clinicalsystem.api.consultation;


import com.clinicalsystem.api.doctor.Doctor;
import com.clinicalsystem.api.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "consultation")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private LocalDateTime consultationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private ConsultationStatus status = ConsultationStatus.SCHEDULED;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    public Consultation(){}
    public Consultation(LocalDateTime consultationDate, Doctor doctor, Patient patient) {
        this.consultationDate = consultationDate;
        this.doctor = doctor;
        this.patient = patient;
    }

}
