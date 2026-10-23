package com.clinicalsystem.api.doctor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Page<Doctor> findBySpecialtyId(Long specialtyId, Pageable pageable);

    boolean existsByCrmAndCrmUf(String crm, String crmUf);

    boolean existsByCrmAndCrmUfAndIdNot(String crm, String crmUf, Long id);

}
