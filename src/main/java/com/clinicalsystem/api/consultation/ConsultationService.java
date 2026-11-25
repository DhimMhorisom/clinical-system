package com.clinicalsystem.api.consultation;

import com.clinicalsystem.api.consultation.dto.ConsultationRequestDTO;
import com.clinicalsystem.api.consultation.dto.ConsultationResponseDTO;
import com.clinicalsystem.api.consultation.dto.ConsultationUpdateRequestDTO;
import com.clinicalsystem.api.doctor.Doctor;
import com.clinicalsystem.api.doctor.DoctorRepository;
import com.clinicalsystem.api.exception.BadRequestException;
import com.clinicalsystem.api.exception.NotFoundException;
import com.clinicalsystem.api.patient.Patient;
import com.clinicalsystem.api.patient.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public ConsultationService(ConsultationRepository consultationRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.consultationRepository = consultationRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public Page<ConsultationResponseDTO> findAll(Long patientId, Long doctorId, Pageable pageable) {

        if(doctorId != null && patientId != null){
            throw new BadRequestException("Use only one filter at a time.");
        }

        if(doctorId != null){
            doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new NotFoundException("Doctor not found."));

            return consultationRepository.findByDoctorId(doctorId, pageable).map(this::convertForResponseDTO);
        }

        if(patientId != null){
            patientRepository.findById(patientId)
                    .orElseThrow(() -> new NotFoundException("Patient not found."));

            return consultationRepository.findByPatientId(patientId, pageable).map(this::convertForResponseDTO);

        }

        return consultationRepository.findAll(pageable)
                .map(this::convertForResponseDTO);
    }

    @Transactional(readOnly = true)
    public ConsultationResponseDTO findById(Long id){
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consultation not found."));

        return convertForResponseDTO(consultation);
    }

    @Transactional
    public ConsultationResponseDTO create(ConsultationRequestDTO dto){

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found."));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new NotFoundException("Patient not found."));

        Consultation consultation = new Consultation(
                dto.getConsultationDate(),
                doctor,
                patient
        );

        Consultation consultationSaved = consultationRepository.save(consultation);

        return convertForResponseDTO(consultationSaved);
    }

    @Transactional
    public ConsultationResponseDTO update(ConsultationUpdateRequestDTO dto, Long id){


        Consultation existingConsultation = consultationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consultation not found."));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found."));

        existingConsultation.setConsultationDate(dto.getConsultationDate());
        existingConsultation.setDoctor(doctor);

        // The patient associated with a consultation cannot be changed.
        // If a different patient needs the appointment, the existing consultation
        // must be cancelled and a new one created.

        Consultation consultationSaved = consultationRepository.save(existingConsultation);

        return convertForResponseDTO(consultationSaved);
    }

    @Transactional
    public void delete(Long id){
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consultation not found."));

        consultationRepository.delete(consultation);
    }

    private ConsultationResponseDTO convertForResponseDTO(Consultation consultation){

        return new ConsultationResponseDTO(
                consultation.getId(),
                consultation.getConsultationDate(),
                consultation.getDoctor().getName(),
                consultation.getPatient().getName(),
                consultation.getStatus()
                );

    }
}
