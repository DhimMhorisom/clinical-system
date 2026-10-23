package com.clinicalsystem.api.doctor;

import com.clinicalsystem.api.doctor.dto.DoctorRequestDTO;
import com.clinicalsystem.api.doctor.dto.DoctorResponseDTO;
import com.clinicalsystem.api.exception.AlreadyExistingException;
import com.clinicalsystem.api.exception.NotFoundException;
import com.clinicalsystem.api.specialty.Specialty;
import com.clinicalsystem.api.specialty.SpecialtyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;

    public DoctorService(DoctorRepository doctorRepository, SpecialtyRepository specialtyRepository) {
        this.doctorRepository = doctorRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponseDTO> findAll(Long specialtyId ,Pageable pageable){

        if(specialtyId == null){
            return doctorRepository.findAll(pageable)
                    .map(this::convertForResponseDTO);
        }

        if (!specialtyRepository.existsById(specialtyId)) {
            throw new NotFoundException("Specialty not found.");
        }

        return doctorRepository.findBySpecialtyId(specialtyId, pageable)
                .map(this::convertForResponseDTO);
    }

    @Transactional(readOnly = true)
    public DoctorResponseDTO findById(Long id){
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor not found."));

        return convertForResponseDTO(doctor);
    }

    @Transactional
    public DoctorResponseDTO create(DoctorRequestDTO dto){

        if(doctorRepository.existsByCrmAndCrmUf(
                dto.getCrm(),
                dto.getCrmUf()
        )) {
            throw new AlreadyExistingException("Doctor with this CRM and UF already exists.");
        }

        Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId())
                .orElseThrow(() -> new NotFoundException("Specialty not found."));

        Doctor doctor = new Doctor(
                dto.getName(),
                dto.getCrm(),
                dto.getCrmUf(),
                specialty);

        Doctor doctorSaved = doctorRepository.save(doctor);

        return convertForResponseDTO(doctorSaved);
    }

    @Transactional
    public DoctorResponseDTO update(DoctorRequestDTO dto , Long id){

        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor not found."));

        if(doctorRepository.existsByCrmAndCrmUfAndIdNot(
                dto.getCrm(),
                dto.getCrmUf(),
                id
        )) {
            throw new AlreadyExistingException("Doctor with this CRM and UF already exists.");
        }

        Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId())
                .orElseThrow(() -> new NotFoundException("Specialty not found."));

        existingDoctor.setName(dto.getName());
        existingDoctor.setCrm(dto.getCrm());
        existingDoctor.setCrmUf(dto.getCrmUf());
        existingDoctor.setSpecialty(specialty);

        Doctor doctorSaved = doctorRepository.save(existingDoctor);

        return convertForResponseDTO(doctorSaved);
    }

    @Transactional
    public void delete(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor not found."));

        doctorRepository.delete(doctor);
    }

    private DoctorResponseDTO convertForResponseDTO(Doctor doctor){

        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getCrm(),
                doctor.getCrmUf(),
                doctor.getSpecialty().getId(),
                doctor.getSpecialty().getName()
        );

    }
}

