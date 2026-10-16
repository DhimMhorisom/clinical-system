package com.clinicalsystem.api.exception.dto;

import lombok.Getter;

@Getter
public class ValidationErrorDTO {

    private final String field;
    private final String message;

    public ValidationErrorDTO(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
