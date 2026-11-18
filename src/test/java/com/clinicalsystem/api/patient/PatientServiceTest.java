package com.clinicalsystem.api.patient;

import com.clinicalsystem.api.exception.AlreadyExistingException;
import com.clinicalsystem.api.exception.NotFoundException;
import com.clinicalsystem.api.patient.dto.PatientRequestDTO;
import com.clinicalsystem.api.patient.dto.PatientResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;


    @Test
    public void shouldReturnAllPatientsPaginated(){

        // Arrange
        Long patientId = 1L;

        Patient patient = new Patient(
                "Walter Withe", "52998224725");

        ReflectionTestUtils.setField(patient , "id" ,patientId);

        Pageable pageable = PageRequest.of(0,10);
        Page<Patient> patientPage = new PageImpl<>(
                List.of(patient),
                pageable,1);

        when(patientRepository.findAll(pageable))
                .thenReturn(patientPage);

        // Act
        Page<PatientResponseDTO> result = patientService.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());

        PatientResponseDTO patientDTO = result.getContent().get(0);

        // Assert
        assertEquals(patientId , patientDTO.getId());
        assertEquals("Walter Withe" , patientDTO.getName());
        assertEquals("52998224725" , patientDTO.getCpf());

        // Verify
        verify(patientRepository).findAll(pageable);
    }

    @Test
    public void shouldReturnPatientWhenIdExists(){

        // Arrange
        Long patientId = 1L;

        Patient patient = new Patient(
                "Walter Withe", "52998224725");

        ReflectionTestUtils.setField(patient , "id" ,patientId);

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(patient));

        // Act
        PatientResponseDTO result = patientService.findById(patientId);

        // Assert
        assertEquals(patientId , result.getId());
        assertEquals("Walter Withe" , result.getName());
        assertEquals("52998224725" , result.getCpf());

        // Verify
        verify(patientRepository).findById(patientId);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenIdDoesNotExist(){

        // Arrange
        Long id = 1L;

        when(patientRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> patientService.findById(id)
        );

        assertEquals("Patient not found." , exception.getMessage());

        // Verify
        verify(patientRepository).findById(id);
    }

    @Test
    public void shouldCreatePatientSuccessfully(){

        // Arrange
        Long patientId = 1L;

        Patient patient = new Patient(
                "Walter Withe", "52998224725"
        );

        ReflectionTestUtils.setField(patient,"id", patientId);

        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setName("Walter Withe");
        dto.setCpf("52998224725");

        when(patientRepository.existsByCpf("52998224725"))
                .thenReturn(false);

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(patient);

        ArgumentCaptor<Patient> patientCaptor =
                ArgumentCaptor.forClass(Patient.class);

        // Act
        PatientResponseDTO result = patientService.create(dto);

        // Assert
        assertEquals(patientId , result.getId());
        assertEquals("Walter Withe" , result.getName());
        assertEquals("52998224725" , result.getCpf());

        // Verify
        verify(patientRepository).existsByCpf("52998224725");
        verify(patientRepository).save(patientCaptor.capture());

        // Assert - captured entity
        Patient capturedPatient = patientCaptor.getValue();

        assertNull(capturedPatient.getId());
        assertEquals("Walter Withe", capturedPatient.getName());
        assertEquals("52998224725", capturedPatient.getCpf());
    }

    @Test
    public void shouldNormalizeCpfBeforeSaving(){

        // Arrange
        Long patientId = 1L;

        Patient patient = new Patient(
                "Walter Withe", "52998224725");

        ReflectionTestUtils.setField(patient , "id" , patientId);

        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setName("Walter Withe");
        dto.setCpf("529.982.247-25");

        when(patientRepository.existsByCpf("52998224725"))
                .thenReturn(false);

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(patient);

        ArgumentCaptor<Patient> patientCaptor =
                ArgumentCaptor.forClass(Patient.class);

        // Act
        PatientResponseDTO result = patientService.create(dto);

        // Verify
        verify(patientRepository).existsByCpf("52998224725");
        verify(patientRepository).save(patientCaptor.capture());

        Patient capturedPatient = patientCaptor.getValue();

        // Assert - captured entity
        assertEquals("Walter Withe", capturedPatient.getName());
        assertEquals("52998224725", capturedPatient.getCpf());

        // Assert - response DTO
        assertEquals(patientId, result.getId());
        assertEquals("Walter Withe", result.getName());
        assertEquals("52998224725", result.getCpf());
    }

    @Test
    public void shouldThrowAlreadyExistingExceptionWhenCpfAlreadyExists(){

        // Arrange
        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setName("Walter Withe");
        dto.setCpf("529.982.247-25");

        when(patientRepository.existsByCpf("52998224725"))
                .thenReturn(true);

        // Act + Asserts
        AlreadyExistingException exception = assertThrows(
                AlreadyExistingException.class,
                () -> patientService.create(dto)
        );

        assertEquals("Patient with this CPF already exists." ,
                exception.getMessage());


        // Verify
        verify(patientRepository).existsByCpf("52998224725");
        verify(patientRepository, never()).save(any(Patient.class));

    }

    @Test
    public void shouldUpdatePatientSuccessfully(){

        // Arrange
        Long patientId = 1L;

        Patient existingPatient = new Patient(
                "Patrick Jane" , "17654464223" );

        ReflectionTestUtils.setField(existingPatient, "id" , patientId);

        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setName("Jesse Pinkman");
        dto.setCpf("28587268686");

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(existingPatient));

        when(patientRepository.existsByCpfAndIdNot("28587268686", patientId))
                .thenReturn(false);

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(existingPatient);

        // Act
        PatientResponseDTO result = patientService.update(dto, patientId);

        // Assert - Entity
        assertEquals(patientId , existingPatient.getId());
        assertEquals("Jesse Pinkman", existingPatient.getName());
        assertEquals("28587268686" , existingPatient.getCpf());

        // Assert - dto
        assertEquals(patientId , result.getId());
        assertEquals("Jesse Pinkman", result.getName());
        assertEquals("28587268686" , result.getCpf());

        // Verify
        verify(patientRepository).findById(patientId);
        verify(patientRepository).existsByCpfAndIdNot("28587268686", patientId);
        verify(patientRepository).save(existingPatient);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdatingNonexistentPatient(){

        // Arrange
        Long patientId = 1L;

        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setName("Walter Withe");
        dto.setCpf("52998224725");

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> patientService.update(dto, patientId)
        );

        assertEquals("Patient not found." , exception.getMessage());

        // Verify
        verify(patientRepository).findById(patientId);
        verify(patientRepository , never()).save(any(Patient.class));
        verify(patientRepository , never()).existsByCpfAndIdNot(anyString(), anyLong());
    }

    @Test
    public void shouldThrowAlreadyExistingExceptionWhenUpdatingWithCpfBelongingToAnotherPatient(){

        // Arrange
        Long patientId = 1L;

        Patient patient = new Patient(
                "Patrick Jane" , "17654464223" );

        ReflectionTestUtils.setField(patient, "id" , patientId);

        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setName("Walter Withe");
        dto.setCpf("52998224725");

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(patient));

        when(patientRepository.existsByCpfAndIdNot("52998224725", patientId))
                .thenReturn(true);

        // Act + Assert
        AlreadyExistingException exception = assertThrows(
                AlreadyExistingException.class,
                () -> patientService.update(dto, patientId)
        );

        assertEquals("Patient with this CPF already exists." , exception.getMessage());

        // Verify
        verify(patientRepository).findById(patientId);
        verify(patientRepository).existsByCpfAndIdNot("52998224725",patientId);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    public void shouldAllowKeepingSameCpfWhenUpdatingPatient(){

        // Arrange
        Long patientId = 1L;

        Patient existingPatient = new Patient(
                "Patrick Jane" , "52998224725" );

        ReflectionTestUtils.setField(existingPatient, "id" , patientId);

        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setName("Walter Withe");
        dto.setCpf("52998224725");

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(existingPatient));

        when(patientRepository.existsByCpfAndIdNot("52998224725", patientId))
                .thenReturn(false);

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(existingPatient);

        // Act
        PatientResponseDTO result = patientService.update(dto, patientId);

        // Assert - Entity
        assertEquals(patientId , existingPatient.getId());
        assertEquals("Walter Withe", existingPatient.getName());
        assertEquals("52998224725" , existingPatient.getCpf());

        // Assert - dto
        assertEquals(patientId , result.getId());
        assertEquals("Walter Withe", result.getName());
        assertEquals("52998224725" , result.getCpf());

        // Verify
        verify(patientRepository).findById(patientId);
        verify(patientRepository).existsByCpfAndIdNot("52998224725", patientId);
        verify(patientRepository).save(existingPatient);
    }

    @Test
    public void shouldNormalizeCpfWhenUpdatingPatient(){

        // Arrange
        Long patientId = 1L;

        Patient existingPatient = new Patient(
                "Walter Withe", "52998224725");

        ReflectionTestUtils.setField(existingPatient , "id" , patientId);

        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setName("Jesse Pinkman");
        dto.setCpf("285.872.686-86");

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(existingPatient));

        when(patientRepository.existsByCpfAndIdNot("28587268686", patientId))
                .thenReturn(false);

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(existingPatient);

        ArgumentCaptor<Patient> patientCaptor =
                ArgumentCaptor.forClass(Patient.class);

        // Act
        PatientResponseDTO result = patientService.update(dto , patientId);

        // Verify
        verify(patientRepository).existsByCpfAndIdNot("28587268686", patientId);
        verify(patientRepository).save(patientCaptor.capture());

        Patient capturedPatient = patientCaptor.getValue();

        // Assert - captured entity
        assertEquals("Jesse Pinkman", capturedPatient.getName());
        assertEquals("28587268686", capturedPatient.getCpf());

        // Assert - response DTO
        assertEquals(patientId, result.getId());
        assertEquals("Jesse Pinkman", result.getName());
        assertEquals("28587268686", result.getCpf());
    }

    @Test
    public void shouldDeletePatientSuccessfully(){

        // Arrange
        Long patientId = 1L;

        Patient patient = new Patient(
                "Walter Withe", "52998224725");

        ReflectionTestUtils.setField(patient , "id" , patientId);

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(patient));

        // Act
        patientService.delete(patientId);

        // Verify
        verify(patientRepository).findById(patientId);
        verify(patientRepository).delete(patient);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenDeletingNonexistentPatient(){

        // Arrange
        Long patientId = 1L;

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> patientService.delete(patientId)
        );

        assertEquals("Patient not found." , exception.getMessage());

        // Verify
        verify(patientRepository).findById(patientId);
        verify(patientRepository, never()).delete(any(Patient.class));
    }

}
