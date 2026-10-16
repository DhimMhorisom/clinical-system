package com.clinicalsystem.api.exception.dto;

import lombok.Getter;

@Getter
public class ErrorResponseDTO {

    private final Integer status;
    private final String message;
    public ErrorResponseDTO(Integer status, String message) {
        this.status = status;
        this.message = message;
    }
}
