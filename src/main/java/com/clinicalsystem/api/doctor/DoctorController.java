package com.clinicalsystem.api.doctor;

import com.clinicalsystem.api.doctor.dto.DoctorRequestDTO;
import com.clinicalsystem.api.doctor.dto.DoctorResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponseDTO>> findAll(@RequestParam(required = false) Long specialtyId, Pageable pageable){

        return ResponseEntity.ok(doctorService.findAll(specialtyId ,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO>findById(@PathVariable Long id){

        DoctorResponseDTO doctor = doctorService.findById(id);

        return ResponseEntity.ok(doctor);
    }

    @PostMapping
    public ResponseEntity<DoctorResponseDTO> create(@RequestBody @Valid DoctorRequestDTO dto){

        DoctorResponseDTO doctorCreate = doctorService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(doctorCreate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> update(@RequestBody @Valid DoctorRequestDTO dto , @PathVariable Long id){

        DoctorResponseDTO updatedDoctor = doctorService.update(dto , id);

        return ResponseEntity.ok(updatedDoctor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        doctorService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
