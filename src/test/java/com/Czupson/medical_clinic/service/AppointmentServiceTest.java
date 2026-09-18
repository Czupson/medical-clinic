package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.appointment.AppointmentDto;
import com.Czupson.medical_clinic.dto.appointment.BookAppointmentCommand;
import com.Czupson.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.Czupson.medical_clinic.exception.appointment.*;
import com.Czupson.medical_clinic.exception.doctor.DoctorNotFoundException;
import com.Czupson.medical_clinic.exception.patient.PatientNotFoundException;
import com.Czupson.medical_clinic.mapper.AppointmentMapper;
import com.Czupson.medical_clinic.model.Appointment;
import com.Czupson.medical_clinic.model.Doctor;
import com.Czupson.medical_clinic.model.Patient;
import com.Czupson.medical_clinic.repository.AppointmentRepository;
import com.Czupson.medical_clinic.repository.DoctorRepository;
import com.Czupson.medical_clinic.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AppointmentServiceTest {
    private AppointmentService appointmentService;
    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private AppointmentMapper appointmentMapper;

    @BeforeEach
    void setUp() {
        appointmentRepository = mock(AppointmentRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        patientRepository = mock(PatientRepository.class);
        appointmentMapper = mock(AppointmentMapper.class);
        appointmentService = new AppointmentService(
                appointmentRepository,
                doctorRepository,
                patientRepository,
                appointmentMapper
        );
    }

    @Test
    void getAppointment_AppointmentExists_AppointmentReturned() {
        // given
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        AppointmentDto appointmentDto = mock(AppointmentDto.class);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);
        // when
        AppointmentDto result = appointmentService.getAppointment(appointmentId);
        // then
        assertSame(appointmentDto, result);
        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentMapper).toDto(appointment);
    }

    @Test
    void getAllAppointments_AppointmentsExist_AppointmentsReturned() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        AppointmentDto appointmentDto = mock(AppointmentDto.class);
        Page<Appointment> appointmentPage = new PageImpl<>(List.of(appointment), pageable, 1);
        when(appointmentRepository.findAll(pageable)).thenReturn(appointmentPage);
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);
        // when
        PageDto<AppointmentDto> result = appointmentService.getAllAppointments(pageable);
        // then
        assertEquals(List.of(appointmentDto), result.content());
        assertEquals(0, result.pageNumber());
        assertEquals(10, result.pageSize());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        verify(appointmentRepository).findAll(pageable);
        verify(appointmentMapper).toDto(appointment);
    }

    @Test
    void addAppointment_ValidCommand_AppointmentCreated() {
        // given
        Long doctorId = 1L;
        LocalDateTime start = LocalDateTime.of(2026, 9, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 1, 11, 0);
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        CreateAppointmentCommand command = new CreateAppointmentCommand(doctorId, start, end);
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setAppointmentStart(start);
        appointment.setAppointmentEnd(end);
        AppointmentDto appointmentDto = mock(AppointmentDto.class);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorAndAppointmentStartLessThanAndAppointmentEndGreaterThan(doctor, end, start)).thenReturn(false);
        when(appointmentMapper.toAppointment(command)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);
        // when
        AppointmentDto result = appointmentService.addAppointment(command);
        // then
        assertSame(appointmentDto, result);
        verify(doctorRepository).findById(doctorId);
        verify(appointmentRepository).existsByDoctorAndAppointmentStartLessThanAndAppointmentEndGreaterThan(doctor, end, start);
        verify(appointmentMapper).toAppointment(command);
        verify(appointmentRepository).save(appointment);
        verify(appointmentMapper).toDto(appointment);
    }

    @Test
    void addAppointment_DoctorDoesNotExist_ExceptionThrown() {
        // given
        Long doctorId = 1L;
        LocalDateTime start = LocalDateTime.of(2026, 9, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 1, 11, 0);
        CreateAppointmentCommand command = new CreateAppointmentCommand(doctorId, start, end);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(DoctorNotFoundException.class, () -> appointmentService.addAppointment(command));
        verify(doctorRepository).findById(doctorId);
        verifyNoInteractions(appointmentRepository, appointmentMapper);
    }

    @Test
    void addAppointment_AppointmentTimeAlreadyTaken_ExceptionThrown() {
        // given
        Long doctorId = 1L;
        LocalDateTime start = LocalDateTime.of(2026, 9, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 1, 11, 0);
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        CreateAppointmentCommand command = new CreateAppointmentCommand(doctorId, start, end);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorAndAppointmentStartLessThanAndAppointmentEndGreaterThan(doctor, end, start)).thenReturn(true);
        // when and then
        assertThrows(AppointmentAlreadyExistsException.class, () -> appointmentService.addAppointment(command));
        verify(doctorRepository).findById(doctorId);
        verify(appointmentRepository).existsByDoctorAndAppointmentStartLessThanAndAppointmentEndGreaterThan(doctor, end, start);
        verifyNoInteractions(appointmentMapper);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_ValidCommand_AppointmentBooked() {
        // given
        Long appointmentId = 1L;
        Long patientId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);
        Patient patient = new Patient();
        patient.setId(patientId);
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentStart(start);
        appointment.setAppointmentEnd(end);
        appointment.setPatient(null);
        BookAppointmentCommand command = new BookAppointmentCommand(patientId);
        AppointmentDto appointmentDto = mock(AppointmentDto.class);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.existsByPatientAndAppointmentStartLessThanAndAppointmentEndGreaterThan(patient, end, start)).thenReturn(false);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);
        // when
        AppointmentDto result = appointmentService.bookAppointment(appointmentId, command);
        // then
        assertSame(appointmentDto, result);
        assertSame(patient, appointment.getPatient());
        verify(appointmentRepository).findById(appointmentId);
        verify(patientRepository).findById(patientId);
        verify(appointmentRepository).existsByPatientAndAppointmentStartLessThanAndAppointmentEndGreaterThan(patient, end, start);
        verify(appointmentRepository).save(appointment);
        verify(appointmentMapper).toDto(appointment);
    }

    @Test
    void bookAppointment_AppointmentAlreadyBooked_ExceptionThrown() {
        // given
        Long appointmentId = 1L;
        Long patientId = 1L;
        Patient currentPatient = new Patient();
        currentPatient.setId(2L);
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentStart(LocalDateTime.now().plusDays(1));
        appointment.setAppointmentEnd(LocalDateTime.now().plusDays(1).plusHours(1));
        appointment.setPatient(currentPatient);
        BookAppointmentCommand command = new BookAppointmentCommand(patientId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        // when and then
        assertThrows(AppointmentAlreadyBookedException.class, () -> appointmentService.bookAppointment(appointmentId, command));
        verify(appointmentRepository).findById(appointmentId);
        verifyNoInteractions(patientRepository, appointmentMapper);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_AppointmentIsInThePast_ExceptionThrown() {
        // given
        Long appointmentId = 1L;
        Long patientId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = start.plusHours(1);
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentStart(start);
        appointment.setAppointmentEnd(end);
        appointment.setPatient(null);
        BookAppointmentCommand command = new BookAppointmentCommand(patientId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        // when and then
        assertThrows(AppointmentDataValidationException.class, () -> appointmentService.bookAppointment(appointmentId, command));
        verify(appointmentRepository).findById(appointmentId);
        verifyNoInteractions(patientRepository, appointmentMapper);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_AppointmentDoesNotExist_ExceptionThrown() {
        // given
        Long appointmentId = 1L;
        Long patientId = 1L;
        BookAppointmentCommand command = new BookAppointmentCommand(patientId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(AppointmentNotFoundException.class, () -> appointmentService.bookAppointment(appointmentId, command));
        verify(appointmentRepository).findById(appointmentId);
        verifyNoInteractions(patientRepository, appointmentMapper);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_PatientDoesNotExist_ExceptionThrown() {
        // given
        Long appointmentId = 1L;
        Long patientId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentStart(start);
        appointment.setAppointmentEnd(end);
        appointment.setPatient(null);
        BookAppointmentCommand command = new BookAppointmentCommand(patientId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(PatientNotFoundException.class, () -> appointmentService.bookAppointment(appointmentId, command));
        verify(appointmentRepository).findById(appointmentId);
        verify(patientRepository).findById(patientId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verifyNoInteractions(appointmentMapper);
    }

    @Test
    void bookAppointment_PatientHasAppointmentAtSameTime_ExceptionThrown() {
        // given
        Long appointmentId = 1L;
        Long patientId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);
        Patient patient = new Patient();
        patient.setId(patientId);
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentStart(start);
        appointment.setAppointmentEnd(end);
        appointment.setPatient(null);
        BookAppointmentCommand command = new BookAppointmentCommand(patientId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.existsByPatientAndAppointmentStartLessThanAndAppointmentEndGreaterThan(patient, end, start)).thenReturn(true);
        // when and then
        assertThrows(PatientAppointmentConflictException.class, () -> appointmentService.bookAppointment(appointmentId, command));
        verify(appointmentRepository).findById(appointmentId);
        verify(patientRepository).findById(patientId);
        verify(appointmentRepository).existsByPatientAndAppointmentStartLessThanAndAppointmentEndGreaterThan(patient, end, start);
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verifyNoInteractions(appointmentMapper);
    }

    @Test
    void getPatientAppointments_PatientExists_AppointmentsReturned() {
        // given
        Long patientId = 1L;
        Patient patient = new Patient();
        patient.setId(patientId);
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        AppointmentDto appointmentDto = mock(AppointmentDto.class);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatient(patient)).thenReturn(List.of(appointment));
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);
        // when
        List<AppointmentDto> result = appointmentService.getPatientAppointments(patientId);
        // then
        assertEquals(List.of(appointmentDto), result);
        verify(patientRepository).findById(patientId);
        verify(appointmentRepository).findByPatient(patient);
        verify(appointmentMapper).toDto(appointment);
    }

    @Test
    void getPatientAppointments_PatientDoesNotExist_ExceptionThrown() {
        // given
        Long patientId = 1L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(PatientNotFoundException.class, () -> appointmentService.getPatientAppointments(patientId));
        verify(patientRepository).findById(patientId);
        verifyNoInteractions(appointmentRepository, appointmentMapper);
    }

    @Test
    void deleteAppointment_AppointmentExists_AppointmentDeleted() {
        // given
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        // when
        appointmentService.deleteAppointment(appointmentId);
        // then
        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void deleteAppointment_AppointmentDoesNotExist_ExceptionThrown() {
        // given
        Long appointmentId = 1L;
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(AppointmentNotFoundException.class, () -> appointmentService.deleteAppointment(appointmentId));
        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository, never()).delete(any(Appointment.class));
    }
}