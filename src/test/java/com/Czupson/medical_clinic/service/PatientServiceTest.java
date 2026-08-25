package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.patient.PatientDto;
import com.Czupson.medical_clinic.dto.patient.UpdatePatientCommand;
import com.Czupson.medical_clinic.exception.patient.PatientAlreadyExistsException;
import com.Czupson.medical_clinic.exception.patient.PatientNotFoundException;
import com.Czupson.medical_clinic.exception.user.UserNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientMapper.toDto(patient)).thenReturn(patientDto);
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
        PatientDto patientDto = new PatientDto(1L, "ABC123456", "Jan", "Kowalski", "123456789", null);
        Page<Patient> patientPage = new PageImpl<>(List.of(patient), pageable, 1);
        when(patientRepository.findAll(pageable)).thenReturn(patientPage);
        when(patientMapper.toDto(patient)).thenReturn(patientDto);
        // when
        PageDto<PatientDto> result = patientService.getAllPatients(pageable);
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
        User user = createUser(userId);
        Patient patient = createPatient(1L, user);
        PatientDto patientDto = mock(PatientDto.class);
        CreatePatientCommand command = new CreatePatientCommand(userId, "ABC123456", "Jan", "Kowalski", "123456789",
                LocalDate.of(1990, 1, 1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(patientMapper.toPatient(command)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toDto(patient)).thenReturn(patientDto);
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
        User user = createUser(1L);
        Patient patient = createPatient(patientId, "ABC123456", "Jan", "Kowalski",
                "123456789", LocalDate.of(1990, 1, 1), user
        );
        UpdatePatientCommand command = new UpdatePatientCommand(patientId, "ABC654321", "Adam",
                "Nowak", "987654321", LocalDate.of(1991, 2, 2));
        Patient updatedPatient = createPatient(patientId, "ABC654321", "Adam", "Nowak",
                "987654321", LocalDate.of(1991, 2, 2), user);
        PatientDto patientDto = new PatientDto(patientId, "ABC654321", "Adam", "Nowak",
                "987654321", LocalDate.of(1991, 2, 2));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientMapper.toPatient(command)).thenReturn(updatedPatient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toDto(patient)).thenReturn(patientDto);
        // when
        PatientDto result = patientService.updatePatient(patientId, command);
        // then
        assertEquals(patientDto, result);
        verify(patientRepository).findById(patientId);
        verify(patientMapper).toPatient(command);
        verify(patientRepository).save(patient);
        verify(patientMapper).toDto(patient);
    }

    @Test
    void getPatient_PatientDoesNotExist_ExceptionThrown() {
        //given
        Long patientId = 1L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());
        //when and then
        assertThrows(PatientNotFoundException.class, () -> patientService.getPatient(patientId));
        verify(patientRepository).findById(patientId);
        verifyNoInteractions(patientMapper);
    }

    @Test
    void addPatient_UserDoesNotExist_ExceptionThrown() {
        //given
        Long userId = 1L;
        CreatePatientCommand command = new CreatePatientCommand(userId, "ABC123456", "Jan",
                "Kowalski", "123456789", LocalDate.of(1990, 1, 1));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        //when and then
        assertThrows(UserNotFoundException.class, () -> patientService.addPatient(command));
        verify(userRepository).findById(userId);
        verifyNoInteractions(patientMapper, patientRepository);
    }

    @Test
    void addPatient_PatientAlreadyExists_ExceptionThrown() {
        //given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("jan.kowalski@example.com");
        Patient existingPatient = new Patient();
        existingPatient.setId(10L);
        user.setPatient(existingPatient);
        CreatePatientCommand command = new CreatePatientCommand(userId, "ABC123456", "Jan",
                "Kowalski", "987456321", LocalDate.of(1990, 1, 1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        //when and then
        assertThrows(PatientAlreadyExistsException.class, () -> patientService.addPatient(command));
        verify(userRepository).findById(userId);
        verifyNoInteractions(patientRepository, patientMapper);
    }

    @Test
    void updatePatient_PatientDoesNotExist_ExceptionThrown() {
        //given
        Long patientId = 1L;
        UpdatePatientCommand command = new UpdatePatientCommand(patientId, "ABC123456", "Jan",
                "Kowalski", "987456321", LocalDate.of(1990, 1, 1));
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());
        //when and then
        assertThrows(PatientNotFoundException.class, () -> patientService.updatePatient(patientId, command));
        verify(patientRepository).findById(patientId);
        verifyNoInteractions(patientMapper);
    }

    @Test
    void deletePatient_PatientExists_PatientDeleted() {
        // given
        Long patientId = 1L;
        Patient patient = new Patient();
        patient.setId(patientId);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        // when
        patientService.deletePatient(patientId);
        // then
        verify(patientRepository).findById(patientId);
        verify(patientRepository).delete(patient);
    }

    private User createUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("jan.kowalski@example.com");
        return user;
    }

    private Patient createPatient(Long patientId, User user) {
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setIdCardNo("ABC123456");
        patient.setFirstName("Jan");
        patient.setLastName("Kowalski");
        patient.setPhoneNumber("123456789");
        patient.setBirthday(LocalDate.of(1990, 1, 1));
        patient.setUser(user);
        return patient;
    }

    private Patient createPatient(
            Long id,
            String idCardNo,
            String firstName,
            String lastName,
            String phoneNumber,
            LocalDate birthday,
            User user) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setIdCardNo(idCardNo);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setPhoneNumber(phoneNumber);
        patient.setBirthday(birthday);
        patient.setUser(user);
        return patient;
    }
}
