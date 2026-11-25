package com.clinicalsystem.api.consultation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation , Long> {

    Page<Consultation>findByDoctorId(Long doctorId, Pageable pageable);
    Page<Consultation>findByPatientId(Long patientId, Pageable pageable);
}
