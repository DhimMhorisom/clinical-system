package com.clinicalsystem.api.exception;

import com.clinicalsystem.api.exception.dto.ErrorResponseDTO;
import com.clinicalsystem.api.exception.dto.ValidationErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundErrors(NotFoundException ex){

        ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AlreadyExistingException.class)
    public ResponseEntity<ErrorResponseDTO> handleAlreadyExistingException(AlreadyExistingException ex){

        ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.CONFLICT.value(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public  ResponseEntity<List<ValidationErrorDTO>> handleValidation(
            MethodArgumentNotValidException ex){

        var fieldErros = ex.getBindingResult().getFieldErrors();

        List<ValidationErrorDTO> erros = new ArrayList<>();

        for(FieldError fieldError : fieldErros){

            ValidationErrorDTO error = new ValidationErrorDTO(fieldError.getField(), fieldError.getDefaultMessage());

            erros.add(error);
        }

        return ResponseEntity.badRequest().body(erros);
    }


}
