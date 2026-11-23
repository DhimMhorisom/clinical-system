package com.clinicalsystem.api.specialty;

import com.clinicalsystem.api.exception.AlreadyExistingException;
import com.clinicalsystem.api.exception.NotFoundException;
import com.clinicalsystem.api.patient.Patient;
import com.clinicalsystem.api.specialty.dto.SpecialtyRequestDTO;
import com.clinicalsystem.api.specialty.dto.SpecialtyResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
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

        // Verify
        verify(specialtyRepository).findById(id);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenIdDoesNotExist(){

        // Arrange
        Long id = 1L;

        when(specialtyRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () ->specialtyService.findById(id)
        );

        // Assert
        assertEquals("Specialty not found.", exception.getMessage());

        // Verify
        verify(specialtyRepository).findById(id);
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

        // Verify
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

        // Verify
        verify(specialtyRepository).existsByName(dto.getName());
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    public void shouldReturnAllSpecialtiesPaginated(){

        // Arrange
        Long specialtyId = 1L;

        Specialty specialty = new Specialty(
                "Cardiology",
                "Heart and blood vessel care"
        );

        ReflectionTestUtils.setField(specialty, "id", specialtyId);

        Pageable pageable = PageRequest.of(0,10);
        Page<Specialty> specialtyPage = new PageImpl<>(
                List.of(specialty),
                pageable,1);

        when(specialtyRepository.findAll(pageable))
                .thenReturn(specialtyPage);

        // Act
        Page<SpecialtyResponseDTO> result = specialtyService.findAll(pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());

        SpecialtyResponseDTO specialtyDTO = result.getContent().get(0);

        assertEquals(specialtyId , specialtyDTO.getId());
        assertEquals("Cardiology" , specialtyDTO.getName());
        assertEquals("Heart and blood vessel care" , specialtyDTO.getDescription());

        verify(specialtyRepository).findAll(pageable);
    }

    @Test
    public void shouldUpdateSpecialtySuccessfully(){

        // Arrange
        Long specialtyId = 1L;

        Specialty existingSpecialty = new Specialty(
                "Cardiology",
                "Heart and blood vessel care"
        );

        ReflectionTestUtils.setField(existingSpecialty, "id", specialtyId);


        SpecialtyRequestDTO dto = new SpecialtyRequestDTO();
        dto.setName("Neurology");
        dto.setDescription("Nervous system care");

        when(specialtyRepository.findById(specialtyId))
                .thenReturn(Optional.of(existingSpecialty));

        when(specialtyRepository.existsByNameAndIdNot("Neurology", specialtyId))
                .thenReturn(false);

        when(specialtyRepository.save(any(Specialty.class)))
                .thenReturn(existingSpecialty);

        // Act
        SpecialtyResponseDTO result = specialtyService.update(dto, specialtyId);

        // Assert - Entity
        assertEquals(specialtyId , existingSpecialty.getId());
        assertEquals("Neurology" , existingSpecialty.getName());
        assertEquals("Nervous system care" , existingSpecialty.getDescription());

        // Assert - dto
        assertEquals(specialtyId , result.getId());
        assertEquals("Neurology" , result.getName());
        assertEquals("Nervous system care" , result.getDescription());

        // Verify
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository).save(existingSpecialty);
        verify(specialtyRepository).existsByNameAndIdNot("Neurology" , specialtyId);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdatingNonexistentSpecialty(){

        // Arrange

        Long specialtyId = 1L;

        SpecialtyRequestDTO dto = new SpecialtyRequestDTO();
        dto.setName("Neurology");
        dto.setDescription("Nervous system care");

        when(specialtyRepository.findById(specialtyId))
                .thenReturn(Optional.empty());

        // Act + Assert

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> specialtyService.update(dto, specialtyId)
        );

        assertEquals("Specialty not found." , exception.getMessage());

        // Verify
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository, never()).save(any(Specialty.class));
        verify(specialtyRepository, never()).existsByNameAndIdNot(anyString(), anyLong());
    }

    @Test
    public void shouldThrowAlreadyExistingExceptionWhenUpdatingWithNameBelongingToAnotherSpecialty(){

        // Arrange
        Long specialtyId = 1L;

        Specialty existingSpecialty = new Specialty(
                "Cardiology",
                "Heart and blood vessel care"
        );

        ReflectionTestUtils.setField(existingSpecialty, "id", specialtyId);

        SpecialtyRequestDTO dto = new SpecialtyRequestDTO();
        dto.setName("Cardiology");
        dto.setDescription("Nervous system care");

        when(specialtyRepository.findById(specialtyId))
                .thenReturn(Optional.of(existingSpecialty));

        when(specialtyRepository.existsByNameAndIdNot("Cardiology" , specialtyId))
                .thenReturn(true);

        // Act + Assert
        AlreadyExistingException exception = assertThrows(
                AlreadyExistingException.class,
                () -> specialtyService.update(dto , specialtyId)
        );

        assertEquals("Specialty already exists." , exception.getMessage());

        // Verify
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository).existsByNameAndIdNot("Cardiology" , specialtyId);
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }

    @Test
    public void shouldAllowKeepingSameNameWhenUpdatingSpecialty(){
        // Arrange
        Long specialtyId = 1L;

        Specialty existingSpecialty = new Specialty(
                "Cardiology",
                "Heart and blood vessel care"
        );

        ReflectionTestUtils.setField(existingSpecialty, "id", specialtyId);

        SpecialtyRequestDTO dto = new SpecialtyRequestDTO();
        dto.setName("Cardiology");
        dto.setDescription("Nervous system care");

        when(specialtyRepository.findById(specialtyId))
                .thenReturn(Optional.of(existingSpecialty));

        when(specialtyRepository.existsByNameAndIdNot("Cardiology" , specialtyId))
                .thenReturn(false);

        when(specialtyRepository.save(any(Specialty.class)))
                .thenReturn(existingSpecialty);

        // Act
        SpecialtyResponseDTO result = specialtyService.update(dto, specialtyId);

        // Assert - Entity
        assertEquals(specialtyId , existingSpecialty.getId());
        assertEquals("Cardiology" , existingSpecialty.getName());
        assertEquals("Nervous system care" , existingSpecialty.getDescription());

        // Assert - dto
        assertEquals(specialtyId , result.getId());
        assertEquals("Cardiology" , result.getName());
        assertEquals("Nervous system care" , result.getDescription());

        // Verify
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository).save(existingSpecialty);
        verify(specialtyRepository).existsByNameAndIdNot("Cardiology" , specialtyId);

    }

    @Test
    public void shouldDeleteSpecialtySuccessfully(){

        // Arrange
        Long specialtyId = 1L;

        Specialty existingSpecialty = new Specialty(
                "Cardiology",
                "Heart and blood vessel care"
        );

        ReflectionTestUtils.setField(existingSpecialty , "id" , specialtyId);

        when(specialtyRepository.findById(specialtyId))
                .thenReturn(Optional.of(existingSpecialty));

        // Act
        specialtyService.delete(specialtyId);

        // Verify
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository).delete(existingSpecialty);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenDeletingNonexistentSpecialty(){

        // Arrange
        Long specialtyId = 1L;

        when(specialtyRepository.findById(specialtyId))
                .thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> specialtyService.delete(specialtyId)
        );

        assertEquals("Specialty not found." , exception.getMessage());

        // Verify
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository, never()).delete(any(Specialty.class));
    }



}
