package com.clinicalsystem.api.specialty.dto;

import lombok.Getter;

@Getter
public class SpecialtyResponseDTO {

    private Long id;
    private String name;
    private String description;

    public SpecialtyResponseDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
