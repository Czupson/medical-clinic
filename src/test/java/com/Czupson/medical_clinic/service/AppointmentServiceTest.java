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
import com.Czupson.medical_clinic.model.AppointmentStatus;
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
        LocalDateTime start = LocalDateTime.of(2026, 10, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 1, 11, 0);
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
        assertEquals(AppointmentStatus.BOOKED, appointment.getStatus());
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
        appointment.setStatus(AppointmentStatus.BOOKED);
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
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(appointment), pageable, 1);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatient(patient, pageable)).thenReturn(page);
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);
        // when
        PageDto<AppointmentDto> result = appointmentService.getPatientAppointments(patientId, pageable);
        // then
        assertEquals(List.of(appointmentDto), result.content());
        assertEquals(0, result.pageNumber());
        assertEquals(10, result.pageSize());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        verify(patientRepository).findById(patientId);
        verify(appointmentRepository).findByPatient(patient, pageable);
        verify(appointmentMapper).toDto(appointment);
    }

    @Test
    void getPatientAppointments_PatientDoesNotExist_ExceptionThrown() {
        // given
        Long patientId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(PatientNotFoundException.class, () -> appointmentService.getPatientAppointments(patientId, pageable));
        verify(patientRepository).findById(patientId);
        verifyNoInteractions(appointmentRepository, appointmentMapper);
    }

    @Test
    void getAvailableAppointmentsForDoctor_AppointmentsExist_AppointmentsReturned() {
        // given
        Long doctorId = 1L;
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setAppointmentStart(LocalDateTime.of(2026, 10, 1, 10, 0));
        appointment.setAppointmentEnd(LocalDateTime.of(2026, 10, 1, 11, 0));
        appointment.setPatient(null);
        AppointmentDto appointmentDto = mock(AppointmentDto.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(appointment), pageable, 1);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatus.AVAILABLE, pageable)).thenReturn(page);
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);
        // when
        PageDto<AppointmentDto> result = appointmentService.getAvailableAppointmentsForDoctor(doctorId, pageable);
        // then
        assertEquals(List.of(appointmentDto), result.content());
        assertEquals(0, result.pageNumber());
        assertEquals(10, result.pageSize());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        verify(doctorRepository).findById(doctorId);
        verify(appointmentRepository).findByDoctorAndStatus(doctor, AppointmentStatus.AVAILABLE, pageable);
        verify(appointmentMapper).toDto(appointment);
    }

    @Test
    void getAvailableAppointmentsBySpecialization_AppointmentsExist_AppointmentsReturned() {
        // given
        String specialization = "Cardiologist";
        LocalDateTime start = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 2, 0, 0);
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setAppointmentStart(LocalDateTime.of(2026, 10, 1, 10, 0));
        appointment.setAppointmentEnd(LocalDateTime.of(2026, 10, 1, 11, 0));
        appointment.setPatient(null);
        AppointmentDto appointmentDto = mock(AppointmentDto.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(appointment), pageable, 1);
        when(appointmentRepository.findAvailableAppointments(AppointmentStatus.AVAILABLE, specialization, start, end, pageable)).thenReturn(page);
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);
        // when
        PageDto<AppointmentDto> result = appointmentService.getAvailableAppointmentsBySpecialization(specialization, start, end, pageable);
        // then
        assertEquals(List.of(appointmentDto), result.content());
        assertEquals(0, result.pageNumber());
        assertEquals(10, result.pageSize());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        verify(appointmentRepository).findAvailableAppointments(AppointmentStatus.AVAILABLE, specialization, start, end, pageable);
        verify(appointmentMapper).toDto(appointment);
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

    @Test
    void cancelAppointment_ShouldSetStatusToCancelled() {
        // given
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentStatus.BOOKED);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        // when
        appointmentService.cancelAppointment(appointmentId);
        // then
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    void cancelAppointment_AppointmentNotBooked_ExceptionThrown() {
        // given
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentStatus.AVAILABLE);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        // when and then
        assertThrows(AppointmentNotBookedException.class, () -> appointmentService.cancelAppointment(appointmentId));
        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
        assertEquals(AppointmentStatus.AVAILABLE, appointment.getStatus());
    }

    @Test
    void getDoctorAppointments_ShouldReturnAppointments() {
        // given
        Long doctorId = 1L;
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        Pageable pageable = PageRequest.of(0, 10);
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.BOOKED);
        Page<Appointment> appointmentPage = new PageImpl<>(List.of(appointment), pageable, 1);
        AppointmentDto appointmentDto = new AppointmentDto(1L, appointment.getAppointmentStart(), appointment.getAppointmentEnd(),
                doctorId, 1L);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor(doctor, pageable)).thenReturn(appointmentPage);
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);
        // when
        PageDto<AppointmentDto> result = appointmentService.getDoctorAppointments(doctorId, pageable);
        // then
        assertEquals(1, result.content().size());
        assertEquals(appointmentDto, result.content().getFirst());
        verify(doctorRepository).findById(doctorId);
        verify(appointmentRepository).findByDoctor(doctor, pageable);
        verify(appointmentMapper).toDto(appointment);
    }

    @Test
    void getDoctorAppointments_DoctorNotFound_ExceptionThrown() {
        // given
        Long doctorId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(DoctorNotFoundException.class, () -> appointmentService.getDoctorAppointments(doctorId, pageable));
        verify(doctorRepository).findById(doctorId);
        verifyNoInteractions(appointmentRepository, appointmentMapper);
    }

    @Test
    void getAppointmentsBySpecializationAndTimeRange_ShouldReturnAppointments() {
        // given
        String specialization = "Kardiolog";
        LocalDateTime start = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 2, 0, 0);
        Pageable pageable = PageRequest.of(0, 10);
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setAppointmentStart(LocalDateTime.of(2026, 10, 1, 10, 0));
        appointment.setAppointmentEnd(LocalDateTime.of(2026, 10, 1, 10, 30));
        Page<Appointment> appointmentPage = new PageImpl<>(List.of(appointment), pageable, 1);
        AppointmentDto appointmentDto = new AppointmentDto(1L, appointment.getAppointmentStart(),
                appointment.getAppointmentEnd(), 1L, 1L);
        when(appointmentRepository.findAppointmentsBySpecializationAndTimeRange(specialization, start, end, pageable)).thenReturn(appointmentPage);
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);
        // when
        PageDto<AppointmentDto> result = appointmentService.getAppointmentsBySpecializationAndTimeRange(specialization,
                        start, end, pageable);
        // then
        assertEquals(1, result.content().size());
        assertEquals(appointmentDto, result.content().getFirst());
        verify(appointmentRepository).findAppointmentsBySpecializationAndTimeRange(specialization, start, end, pageable);
        verify(appointmentMapper).toDto(appointment);
    }
}