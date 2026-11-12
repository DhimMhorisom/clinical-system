package com.clinicalsystem.api.patient;

import com.clinicalsystem.api.patient.dto.PatientRequestDTO;
import com.clinicalsystem.api.patient.dto.PatientResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<Page<PatientResponseDTO>> findAll(Pageable pageable){
        return ResponseEntity.ok(patientService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> findById(@PathVariable Long id){

        PatientResponseDTO patient = patientService.findById(id);

        return ResponseEntity.ok(patient);
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> create(@RequestBody @Valid PatientRequestDTO dto){

        PatientResponseDTO patientCreated = patientService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(patientCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> update(@RequestBody @Valid PatientRequestDTO dto, @PathVariable Long id){

        PatientResponseDTO patientUpdated = patientService.update(dto, id);

        return ResponseEntity.ok(patientUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        patientService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
