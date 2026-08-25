package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.patient.PatientDto;
import com.Czupson.medical_clinic.dto.patient.UpdatePatientCommand;
import com.Czupson.medical_clinic.mapper.PatientMapper;
import com.Czupson.medical_clinic.model.Patient;
import com.Czupson.medical_clinic.repository.PatientRepository;
import com.Czupson.medical_clinic.repository.UserRepository;
import com.Czupson.medical_clinic.dto.patient.CreatePatientCommand;
import com.Czupson.medical_clinic.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.Czupson.medical_clinic.dto.PageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

public class PatientServiceTest {
    private PatientService patientService;
    private PatientRepository patientRepository;
    private PatientMapper patientMapper;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        patientRepository = mock(PatientRepository.class);
        patientMapper = mock(PatientMapper.class);
        userRepository = mock(UserRepository.class);
        patientService = new PatientService(
                patientRepository,
                patientMapper,
                userRepository);
    }

    @Test
    void getPatient_PatientExists_PatientReturned() {
        //given
        Long patientId = 1L;
        Patient patient = new Patient();
        patient.setId(patientId);
        PatientDto patientDto = mock(PatientDto.class);
        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(patient));
        when(patientMapper.toDto(patient))
                .thenReturn(patientDto);
        // when
        PatientDto result = patientService.getPatient(patientId);
        // then
        assertSame(patientDto, result);
        verify(patientRepository).findById(patientId);
        verify(patientMapper).toDto(patient);
    }

    @Test
    void getAllPatients_PatientsExist_PatientsReturned() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Patient patient = new Patient();
        patient.setId(1L);
        PatientDto patientDto = new PatientDto(
                1L,
                "ABC123456",
                "Jan",
                "Kowalski",
                "123456789",
                null
        );
        Page<Patient> patientPage = new PageImpl<>(
                List.of(patient),
                pageable,
                1
        );
        when(patientRepository.findAll(pageable))
                .thenReturn(patientPage);

        when(patientMapper.toDto(patient))
                .thenReturn(patientDto);
        // when
        PageDto<PatientDto> result =
                patientService.getAllPatients(pageable);
        // then
        assertEquals(List.of(patientDto), result.content());
        assertEquals(0, result.pageNumber());
        assertEquals(10, result.pageSize());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        verify(patientRepository).findAll(pageable);
        verify(patientMapper).toDto(patient);
    }

    @Test
    void addPatient_ValidCommand_PatientCreated() {
        // given
        Long userId = 1L;
        CreatePatientCommand command = new CreatePatientCommand(
                userId,
                "ABC123456",
                "Jan",
                "Kowalski",
                "123456789",
                null
        );
        User user = new User();
        user.setId(userId);
        user.setEmail("jan.kowalski@example.com");
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setIdCardNo("ABC123456");
        patient.setFirstName("Jan");
        patient.setLastName("Kowalski");
        patient.setPhoneNumber("123456789");
        patient.setBirthday(LocalDate.of(1990, 1, 1));
        patient.setUser(user);
        PatientDto patientDto = new PatientDto(
                1L,
                "ABC123456",
                "Jan",
                "Kowalski",
                "123456789",
                null
        );
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(patientMapper.toPatient(command))
                .thenReturn(patient);
        when(patientRepository.save(patient))
                .thenReturn(patient);
        when(patientMapper.toDto(patient))
                .thenReturn(patientDto);
        // when
        PatientDto result = patientService.addPatient(command);
        // then
        assertEquals(patientDto, result);
        verify(userRepository).findById(userId);
        verify(patientMapper).toPatient(command);
        verify(patientRepository).save(patient);
        verify(patientMapper).toDto(patient);
    }

    @Test
    void updatePatient_ValidCommand_PatientUpdated() {
        // given
        Long patientId = 1L;
        User user = new User();
        user.setId(1L);
        user.setEmail("jan.kowalski@example.com");
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setIdCardNo("ABC123456");
        patient.setFirstName("Jan");
        patient.setLastName("Kowalski");
        patient.setPhoneNumber("123456789");
        patient.setBirthday(LocalDate.of(1990, 1, 1));
        patient.setUser(user);
        UpdatePatientCommand command = new UpdatePatientCommand(
                patientId,
                "ABC654321",
                "Adam",
                "Nowak",
                "987654321",
                LocalDate.of(1991, 2, 2)
        );
        Patient updatedPatient = new Patient();
        updatedPatient.setId(patientId);
        updatedPatient.setIdCardNo("ABC654321");
        updatedPatient.setFirstName("Adam");
        updatedPatient.setLastName("Nowak");
        updatedPatient.setPhoneNumber("987654321");
        updatedPatient.setBirthday(LocalDate.of(1991, 2, 2));
        updatedPatient.setUser(user);
        PatientDto patientDto = new PatientDto(
                patientId,
                "ABC654321",
                "Adam",
                "Nowak",
                "987654321",
                LocalDate.of(1991, 2, 2)
        );
        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(patient));

        when(patientMapper.toPatient(command))
                .thenReturn(updatedPatient);

        when(patientRepository.save(patient))
                .thenReturn(patient);
        when(patientMapper.toDto(patient))
                .thenReturn(patientDto);
        // when
        PatientDto result = patientService.updatePatient(patientId, command);
        // then
        assertEquals(patientDto, result);
        verify(patientRepository).findById(patientId);
        verify(patientMapper).toPatient(command);
        verify(patientRepository).save(patient);
        verify(patientMapper).toDto(patient);
    }
}
