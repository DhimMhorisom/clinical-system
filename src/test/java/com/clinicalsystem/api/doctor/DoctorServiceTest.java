package com.clinicalsystem.api.doctor;

import com.clinicalsystem.api.doctor.dto.DoctorRequestDTO;
import com.clinicalsystem.api.doctor.dto.DoctorResponseDTO;
import com.clinicalsystem.api.exception.AlreadyExistingException;
import com.clinicalsystem.api.exception.NotFoundException;
import com.clinicalsystem.api.specialty.Specialty;
import com.clinicalsystem.api.specialty.SpecialtyRepository;
import com.clinicalsystem.api.specialty.SpecialtyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    public void shouldReturnDoctorWhenIdExists(){

        // Arrange
        Long doctorId  = 1L;
        Long specialtyId = 2L;

        Specialty specialty = new Specialty(
                "Cardiology" , "Heart and blood vessel care");
        ReflectionTestUtils.setField(specialty, "id" , specialtyId);

        Doctor doctor = new Doctor(
                "Arthur Morgan" , "123456" , "SP" , specialty);

        ReflectionTestUtils.setField(doctor , "id" , doctorId);

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        // Act
        DoctorResponseDTO result = doctorService.findById(doctorId);

        // Assert
        assertEquals(doctorId , result.getId());
        assertEquals("Arthur Morgan" , result.getName());
        assertEquals("123456" , result.getCrm());
        assertEquals("SP" , result.getCrmUf());
        assertEquals(specialtyId ,result.getSpecialtyId());
        assertEquals("Cardiology" , result.getSpecialty());

        verify(doctorRepository).findById(doctorId);

    }

    @Test
    public void shouldThrowNotFoundExceptionWhenDoctorDoesNotExist(){

        // Arrange
        Long id = 1L;

        when(doctorRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> doctorService.findById(id)
        );

        assertEquals("Doctor not found." , exception.getMessage());

        // Verify
        verify(doctorRepository).findById(id);
    }

    @Test
    public void shouldThrowAlreadyExistingExceptionWhenCrmAndUfAlreadyExist(){

        // Arrange
        DoctorRequestDTO dto = new DoctorRequestDTO();
        dto.setName("Ramón Valdés");
        dto.setCrm("234567");
        dto.setCrmUf("MG");
        dto.setSpecialtyId(3L);

        when(doctorRepository.existsByCrmAndCrmUf("234567" , "MG"))
                .thenReturn(true);

        // Act + Assert
        AlreadyExistingException exception = assertThrows(
                AlreadyExistingException.class,
                () -> doctorService.create(dto)
        );

        assertEquals(
                "Doctor with this CRM and UF already exists.",
                exception.getMessage());

        // Verify
        verify(doctorRepository)
                .existsByCrmAndCrmUf("234567" , "MG");

        verify(doctorRepository, never())
                .save(any(Doctor.class));

    }

    @Test
    public void shouldCreateDoctorSuccessfully(){

        // Arrange
        Long doctorId = 1L;
        Long specialtyId = 2L;

        Specialty specialty = new Specialty(
                "Cardiology",
                "Heart and blood vessel care"
        );
        ReflectionTestUtils.setField(specialty, "id", specialtyId);

        Doctor doctor = new Doctor(
                "Arthur Morgan",
                "123456",
                "SP",
                specialty
        );
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        DoctorRequestDTO dto = new DoctorRequestDTO();
        dto.setName("Arthur Morgan");
        dto.setCrm("123456");
        dto.setCrmUf("SP");
        dto.setSpecialtyId(specialtyId);

        when(doctorRepository.existsByCrmAndCrmUf("123456", "SP"))
                .thenReturn(false);
        when(specialtyRepository.findById(specialtyId))
                .thenReturn(Optional.of(specialty));
        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(doctor);

        ArgumentCaptor<Doctor> doctorCaptor =
                ArgumentCaptor.forClass(Doctor.class);

        // Act
        DoctorResponseDTO result = doctorService.create(dto);

        // Assert
        assertEquals(doctorId, result.getId());
        assertEquals("Arthur Morgan", result.getName());
        assertEquals("123456", result.getCrm());
        assertEquals("SP", result.getCrmUf());
        assertEquals(specialtyId, result.getSpecialtyId());
        assertEquals("Cardiology", result.getSpecialty());

        // Verify
        verify(doctorRepository).existsByCrmAndCrmUf("123456", "SP");
        verify(specialtyRepository).findById(specialtyId);
        verify(doctorRepository).save(doctorCaptor.capture());

        // Assert - captured entity
        Doctor capturedDoctor = doctorCaptor.getValue();

        assertNull(capturedDoctor.getId());
        assertEquals("Arthur Morgan", capturedDoctor.getName());
        assertEquals("123456", capturedDoctor.getCrm());
        assertEquals("SP", capturedDoctor.getCrmUf());
        assertNotNull(capturedDoctor.getSpecialty());
        assertEquals(specialtyId, capturedDoctor.getSpecialty().getId());
        assertEquals("Cardiology", capturedDoctor.getSpecialty().getName());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenSpecialtyDoesNotExist(){

        // Arrange
        Long specialtyId = 2L;

        DoctorRequestDTO dto = new DoctorRequestDTO();
        dto.setName("Ramón Valdés");
        dto.setCrm("234567");
        dto.setCrmUf("MG");
        dto.setSpecialtyId(specialtyId);

        when(doctorRepository.existsByCrmAndCrmUf("234567", "MG"))
                .thenReturn(false);
        when(specialtyRepository.findById(specialtyId))
                .thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> doctorService.create(dto)
        );

        assertEquals("Specialty not found.", exception.getMessage());

        // Verify
        verify(doctorRepository).existsByCrmAndCrmUf("234567", "MG");
        verify(specialtyRepository).findById(specialtyId);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdatingNonExistingDoctor(){

        // Arrange
        Long doctorId = 1L;

        DoctorRequestDTO dto = new DoctorRequestDTO();
        dto.setName("Arthur Morgan");
        dto.setCrm("123456");
        dto.setCrmUf("SP");
        dto.setSpecialtyId(2L);

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> doctorService.update(dto, doctorId)
        );

        assertEquals("Doctor not found.", exception.getMessage());

        // Verify
        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository, never()).save(any(Doctor.class));
        verifyNoInteractions(specialtyRepository);


    }

    @Test
    public void shouldThrowAlreadyExistingExceptionWhenUpdatingDoctorWithDuplicateCrmAndUf(){

        // Arrange
        Long doctorId = 1L;
        Long specialtyId = 2L;

        Specialty specialty = new Specialty(
                "Cardiology",
                "Heart and blood vessel care"
        );
        ReflectionTestUtils.setField(specialty, "id", specialtyId);

        Doctor existingDoctor = new Doctor(
                "Arthur Morgan",
                "123456",
                "SP",
                specialty
        );
        ReflectionTestUtils.setField(existingDoctor, "id", doctorId);

        DoctorRequestDTO dto = new DoctorRequestDTO();
        dto.setName("Arthur Morgan");
        dto.setCrm("123456");
        dto.setCrmUf("SP");
        dto.setSpecialtyId(specialtyId);

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(existingDoctor));
        when(doctorRepository.existsByCrmAndCrmUfAndIdNot("123456", "SP", doctorId))
                .thenReturn(true);

        // Act + Assert
        AlreadyExistingException exception = assertThrows(
                AlreadyExistingException.class,
                () -> doctorService.update(dto, doctorId)
        );

        assertEquals(
                "Doctor with this CRM and UF already exists.",
                exception.getMessage()
        );

        // Verify
        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository).existsByCrmAndCrmUfAndIdNot("123456", "SP", doctorId);
        verify(doctorRepository, never()).save(any(Doctor.class));
        verifyNoInteractions(specialtyRepository);
    }

    @Test
    public void shouldUpdateDoctorSuccessfully(){

        // Arrange
        Long doctorId = 1L;
        Long oldSpecialtyId = 2L;
        Long newSpecialtyId = 3L;

        Specialty oldSpecialty = new Specialty(
                "Cardiology" , "Heart and blood vessel care");

        ReflectionTestUtils.setField(oldSpecialty , "id" , oldSpecialtyId);

        Specialty newSpecialty = new Specialty(
                "Dermatology" , "Skin care"
        );

        ReflectionTestUtils.setField(newSpecialty , "id" , newSpecialtyId);

        Doctor existingDoctor = new Doctor(
                "Arthur Morgan" , "123456" , "SP" , oldSpecialty);

        ReflectionTestUtils.setField(existingDoctor, "id" , doctorId);


        DoctorRequestDTO dto = new DoctorRequestDTO();
        dto.setName("John Marston");
        dto.setCrm("234567");
        dto.setCrmUf("RJ");
        dto.setSpecialtyId(newSpecialtyId);

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(existingDoctor));

        when(doctorRepository.existsByCrmAndCrmUfAndIdNot("234567" , "RJ", doctorId))
                .thenReturn(false);

        when(specialtyRepository.findById(newSpecialtyId))
                .thenReturn(Optional.of(newSpecialty));

        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(existingDoctor);

        // Act

        DoctorResponseDTO result = doctorService.update(dto, doctorId);

        // Assert - updated entity
        assertEquals(doctorId, existingDoctor.getId());
        assertEquals("John Marston" , existingDoctor.getName());
        assertEquals("234567" , existingDoctor.getCrm());
        assertEquals("RJ" , existingDoctor.getCrmUf());
        assertEquals(newSpecialtyId , existingDoctor.getSpecialty().getId());
        assertEquals("Dermatology", existingDoctor.getSpecialty().getName());
        assertSame(newSpecialty, existingDoctor.getSpecialty());

        // Assert - returned DTO
        assertEquals(doctorId, result.getId());
        assertEquals("John Marston" , result.getName());
        assertEquals("234567" , result.getCrm());
        assertEquals("RJ" , result.getCrmUf());
        assertEquals(newSpecialtyId , result.getSpecialtyId());
        assertEquals("Dermatology" , result.getSpecialty());


        // Verify
        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository).existsByCrmAndCrmUfAndIdNot("234567" , "RJ" , doctorId);
        verify(specialtyRepository).findById(newSpecialtyId);
        verify(doctorRepository).save(existingDoctor);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdatingDoctorWithNonExistingSpecialty(){

        // Arrange
        Long doctorId = 1L;
        Long specialtyId = 2L;
        Long nonExistingSpecialtyId = 99L;

        Specialty specialty = new Specialty(
                "Cardiology" , "Heart and blood vessel care");

        ReflectionTestUtils.setField(specialty , "id" , specialtyId);

        Doctor doctor = new Doctor(
                "Arthur Morgan" , "123456" , "SP" , specialty);

        ReflectionTestUtils.setField(doctor, "id" , doctorId);

        DoctorRequestDTO dto = new DoctorRequestDTO();
        dto.setName("Ramón Valdés");
        dto.setCrm("234567");
        dto.setCrmUf("MG");
        dto.setSpecialtyId(nonExistingSpecialtyId);

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(doctorRepository.existsByCrmAndCrmUfAndIdNot("234567", "MG" , doctorId))
                .thenReturn(false);

        when(specialtyRepository.findById(nonExistingSpecialtyId))
                .thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> doctorService.update(dto , doctorId)
        );

        assertEquals("Specialty not found." , exception.getMessage());

        assertEquals("Arthur Morgan" , doctor.getName());
        assertEquals("123456", doctor.getCrm());
        assertEquals("SP" , doctor.getCrmUf());
        assertSame(specialty , doctor.getSpecialty());

        // Verify
        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository).existsByCrmAndCrmUfAndIdNot("234567" , "MG" , doctorId);
        verify(specialtyRepository).findById(nonExistingSpecialtyId);
        verify(doctorRepository , never()).save(any(Doctor.class));

    }

    @Test
    public void shouldDeleteDoctorSuccessfully(){

        // Arrange
        Long doctorId = 1L;

        Doctor doctor = mock(Doctor.class);

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        // Act
        doctorService.delete(doctorId);

        // Verify
        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository).delete(doctor);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenDeletingNonExistingDoctor(){

        // Arrange
        Long doctorId = 1L;

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class ,
                () -> doctorService.delete(doctorId)
        );

        assertEquals("Doctor not found." , exception.getMessage());

        // Verify
        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository , never()).deleteById(doctorId);

    }


    @Test
    public void shouldReturnAllDoctorsWhenSpecialtyIdIsNull(){

        // Arrange
        Long doctorId = 1L;
        Long specialtyId = 2L;

        Specialty specialty = new Specialty(
                "Cardiology",
                "Heart and blood vessel care"
        );
        ReflectionTestUtils.setField(specialty, "id", specialtyId);

        Doctor doctor = new Doctor(
                "Arthur Morgan",
                "123456",
                "SP",
                specialty
        );
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Doctor> doctorPage = new PageImpl<>(List.of(doctor));

        when(doctorRepository.findAll(pageable))
                .thenReturn(doctorPage);

        // Act
        Page<DoctorResponseDTO> result = doctorService.findAll(null, pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalElements());

        DoctorResponseDTO doctorDTO = result.getContent().get(0);

        assertEquals("Arthur Morgan", doctorDTO.getName());
        assertEquals("123456", doctorDTO.getCrm());
        assertEquals("SP", doctorDTO.getCrmUf());
        assertEquals(specialtyId, doctorDTO.getSpecialtyId());
        assertEquals("Cardiology", doctorDTO.getSpecialty());

        // Verify
        verify(doctorRepository).findAll(pageable);
        verifyNoInteractions(specialtyRepository);
    }

    @Test
    public void shouldReturnDoctorsBySpecialtyWhenSpecialtyIdIsProvided(){

        // Arrange
        Long doctorId = 1L;
        Long specialtyId = 2L;

        Specialty specialty = new Specialty(
                "Cardiology",
                "Heart and blood vessel care"
        );
        ReflectionTestUtils.setField(specialty, "id", specialtyId);

        Doctor doctor = new Doctor(
                "Arthur Morgan",
                "123456",
                "SP",
                specialty
        );
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Doctor> doctorPage = new PageImpl<>(List.of(doctor));

        when(specialtyRepository.existsById(specialtyId))
                .thenReturn(true);
        when(doctorRepository.findBySpecialtyId(specialtyId, pageable))
                .thenReturn(doctorPage);

        // Act
        Page<DoctorResponseDTO> result = doctorService.findAll(specialtyId, pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalElements());

        DoctorResponseDTO doctorDTO = result.getContent().get(0);

        assertEquals("Arthur Morgan", doctorDTO.getName());
        assertEquals("123456", doctorDTO.getCrm());
        assertEquals("SP", doctorDTO.getCrmUf());
        assertEquals(specialtyId, doctorDTO.getSpecialtyId());
        assertEquals("Cardiology", doctorDTO.getSpecialty());

        // Verify
        verify(specialtyRepository).existsById(specialtyId);;
        verify(doctorRepository).findBySpecialtyId(specialtyId, pageable);
        verify(doctorRepository, never()).findAll(pageable);

    }

    @Test
    public void shouldThrowNotFoundExceptionWhenFilteringByNonExistingSpecialty() {

        // Arrange
        Long nonExistingSpecialtyId = 99L;

        Pageable pageable = PageRequest.of(0, 10);

        when(specialtyRepository.existsById(nonExistingSpecialtyId))
                .thenReturn(false);

        // Act + Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> doctorService.findAll(nonExistingSpecialtyId, pageable)
        );

        assertEquals("Specialty not found.", exception.getMessage());

        // Verify
        verify(specialtyRepository).existsById(nonExistingSpecialtyId);

        verify(doctorRepository, never())
                .findBySpecialtyId(nonExistingSpecialtyId, pageable);
    }




}
