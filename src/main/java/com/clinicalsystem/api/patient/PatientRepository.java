package com.clinicalsystem.api.patient;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf , Long id);

}
