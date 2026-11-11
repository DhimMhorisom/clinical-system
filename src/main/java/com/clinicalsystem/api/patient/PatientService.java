package com.clinicalsystem.api.patient;

import com.clinicalsystem.api.exception.AlreadyExistingException;
import com.clinicalsystem.api.exception.NotFoundException;
import com.clinicalsystem.api.patient.dto.PatientRequestDTO;
import com.clinicalsystem.api.patient.dto.PatientResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> findAll(Pageable pageable){
        return patientRepository.findAll(pageable)
                .map(this::convertForResponseDTO);
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO findById(Long id){
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found."));

        return convertForResponseDTO(patient);
    }

    @Transactional
    public PatientResponseDTO create(PatientRequestDTO dto){

        // Remove non-numeric characters from the CPF, keeping only the 11 digits.
        String cpf = dto.getCpf().replaceAll("\\D", "");

        if(patientRepository.existsByCpf(cpf)) {
            throw new AlreadyExistingException("Patient with this CPF already exists.");
        }

        Patient patient = new Patient(
                dto.getName(),cpf);

        Patient patientSaved = patientRepository.save(patient);

        return convertForResponseDTO(patientSaved);
    }

    @Transactional
    public PatientResponseDTO update(PatientRequestDTO dto, Long id){

        String cpf = dto.getCpf().replaceAll("\\D", "");

        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found."));

        if(patientRepository.existsByCpfAndIdNot(cpf,id)){
            throw new AlreadyExistingException("Patient with this CPF already exists.");
        }

        existingPatient.setName(dto.getName());
        existingPatient.setCpf(cpf);

        Patient patientSaved = patientRepository.save(existingPatient);

        return convertForResponseDTO(patientSaved);
    }

    @Transactional
    public void delete(Long id){
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found."));

        patientRepository.delete(patient);
    }


    private PatientResponseDTO convertForResponseDTO(Patient patient){

        return new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getCpf()
        );
    }


}
