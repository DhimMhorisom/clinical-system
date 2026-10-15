package com.clinicalsystem.api.specialty;

import com.clinicalsystem.api.exception.AlreadyExistingException;
import com.clinicalsystem.api.exception.NotFoundException;
import com.clinicalsystem.api.specialty.dto.SpecialtyRequestDTO;
import com.clinicalsystem.api.specialty.dto.SpecialtyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;



    public SpecialtyService(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    public Page<SpecialtyResponseDTO> findAll(Pageable pageable){
        return specialtyRepository.findAll(pageable).map(this::convertForResponseDTO);

    }

    public SpecialtyResponseDTO findById(Long id){
        Specialty specialty = specialtyRepository.findById(id).orElseThrow(() -> new NotFoundException("Specialty not found."));

        return convertForResponseDTO(specialty);
    }

    public SpecialtyResponseDTO create(SpecialtyRequestDTO dto){

        if(specialtyRepository.existsByName(dto.getName())){
            throw new AlreadyExistingException("Specialty already exists.");
        }
        Specialty specialty = new Specialty(dto.getName() ,dto.getDescription());

        Specialty specialtySaved = specialtyRepository.save(specialty);

        return  convertForResponseDTO(specialtySaved);

    }

    public SpecialtyResponseDTO update(SpecialtyRequestDTO dto, Long id){

        Specialty existingSpecialty = specialtyRepository.findById(id).orElseThrow(() -> new NotFoundException("Specialty not found."));

        if(specialtyRepository.existsByNameAndIdNot(dto.getName(), id)){
            throw new AlreadyExistingException("Specialty already exists.");
        }

        existingSpecialty.setName(dto.getName());
        existingSpecialty.setDescription(dto.getDescription());

        Specialty specialtySaved = specialtyRepository.save(existingSpecialty);

        return convertForResponseDTO(specialtySaved);
    }

    public void delete(Long id){
        specialtyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Specialty not found"));

        specialtyRepository.deleteById(id);
    }

    private SpecialtyResponseDTO convertForResponseDTO(Specialty specialty){

        return new SpecialtyResponseDTO(
                specialty.getId(),
                specialty.getName(),
                specialty.getDescription()
        );
    }

}
