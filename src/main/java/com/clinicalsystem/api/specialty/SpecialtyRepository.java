package com.clinicalsystem.api.specialty;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);


}
