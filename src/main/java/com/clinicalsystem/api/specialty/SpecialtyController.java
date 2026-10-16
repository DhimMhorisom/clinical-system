package com.clinicalsystem.api.specialty;

import com.clinicalsystem.api.specialty.dto.SpecialtyRequestDTO;
import com.clinicalsystem.api.specialty.dto.SpecialtyResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public ResponseEntity<Page<SpecialtyResponseDTO>> findAll(Pageable pageable){
        return ResponseEntity.ok(specialtyService.findAll(pageable));

    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDTO> findById(@PathVariable Long id){
         SpecialtyResponseDTO specialty = specialtyService.findById(id);

         return ResponseEntity.ok(specialty);
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponseDTO> create (@RequestBody @Valid SpecialtyRequestDTO dto){

        SpecialtyResponseDTO specialtyCreated = specialtyService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(specialtyCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDTO> update (@RequestBody @Valid SpecialtyRequestDTO dto , @PathVariable Long id){

        SpecialtyResponseDTO updatedSpecialty = specialtyService.update(dto, id);

        return ResponseEntity.ok(updatedSpecialty);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        specialtyService.delete(id);

        return ResponseEntity.noContent().build();
    }





}
