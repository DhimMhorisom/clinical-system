package com.clinicalsystem.api.specialty;

import com.clinicalsystem.api.exception.AlreadyExistingException;
import com.clinicalsystem.api.exception.NotFoundException;
import com.clinicalsystem.api.specialty.dto.SpecialtyRequestDTO;
import com.clinicalsystem.api.specialty.dto.SpecialtyResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private SpecialtyService specialtyService;

    @Test
    public void shouldReturnSpecialtyWhenIdExists(){

        // Arrange
        Long id = 1L;

        Specialty specialty = new Specialty(
                "Cardiology" , "Heart and blood vessel care");

        ReflectionTestUtils.setField(specialty , "id" , id);

        when(specialtyRepository.findById(id))
                .thenReturn(Optional.of(specialty));

        // Act
        SpecialtyResponseDTO result = specialtyService.findById(id);

        // Assert
        assertEquals(id, result.getId());
        assertEquals("Cardiology", result.getName());
        assertEquals("Heart and blood vessel care", result.getDescription());

        verify(specialtyRepository).findById(id);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenIdDoesNotExist(){

        // Arrange
        when(specialtyRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () ->specialtyService.findById(1L)
        );

        // Assert
        assertEquals("Specialty not found.", exception.getMessage());
    }

    @Test
    public void shouldThrowAlreadyExistingExceptionWhenSpecialtyNameExists() {

        // Arrange
        SpecialtyRequestDTO dto = new SpecialtyRequestDTO();
        dto.setName("Cardiology");
        dto.setDescription("Heart and blood vessel care");

        when(specialtyRepository.existsByName(dto.getName()))
                .thenReturn(true);

        // Act
        AlreadyExistingException exception = assertThrows(
                AlreadyExistingException.class,
                () -> specialtyService.create(dto)
        );

        // Assert
        assertEquals("Specialty already exists.", exception.getMessage());

        verify(specialtyRepository).existsByName(dto.getName());
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }

    @Test
    void shouldCreateSpecialtyWhenNameDoesNotExist() {

        // Arrange
        Long id = 1L;

        SpecialtyRequestDTO dto = new SpecialtyRequestDTO();
        dto.setName("Cardiology");
        dto.setDescription("Heart and blood vessel care");

        Specialty savedSpecialty = new Specialty(
                "Cardiology",
                "Heart and blood vessel care"
        );

        ReflectionTestUtils.setField(savedSpecialty, "id", id);

        when(specialtyRepository.existsByName(dto.getName()))
                .thenReturn(false);

        when(specialtyRepository.save(any(Specialty.class)))
                .thenReturn(savedSpecialty);

        // Act
        SpecialtyResponseDTO result = specialtyService.create(dto);

        // Assert
        assertEquals(id, result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());

        verify(specialtyRepository).existsByName(dto.getName());
        verify(specialtyRepository).save(any(Specialty.class));
    }

}
