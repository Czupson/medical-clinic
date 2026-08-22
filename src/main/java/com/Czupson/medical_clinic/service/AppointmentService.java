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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional(readOnly = true)
    public PageDto<AppointmentDto> getAllAppointments(Pageable pageable) {
        return PageDto.from(
                appointmentRepository.findAll(pageable)
                        .map(appointmentMapper::toDto)
        );
    }

    @Transactional(readOnly = true)
    public AppointmentDto getAppointment(Long id) {
        return appointmentMapper.toDto(findAppointment(id));
    }

    @Transactional
    public AppointmentDto addAppointment(CreateAppointmentCommand command) {
        log.info("Creating appointment: doctorId={}, start={}, end={}", command.doctorId(), command.appointmentStart(), command.appointmentEnd());
        Doctor doctor = findDoctor(command.doctorId());
        validateAppointmentTimeAvailability(
                doctor,
                command.appointmentStart(),
                command.appointmentEnd()
        );
        Appointment appointment = appointmentMapper.toAppointment(command);
        appointment.setDoctor(doctor);
        appointment.setPatient(null);
        appointment.validate();
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created: id={}, doctorId={}, start={}, end={}", savedAppointment.getId(), doctor.getId(), savedAppointment.getAppointmentStart(), savedAppointment.getAppointmentEnd());
        return appointmentMapper.toDto(savedAppointment);
    }

    @Transactional
    public AppointmentDto bookAppointment(
            Long appointmentId,
            BookAppointmentCommand command) {
        log.info("Booking appointment: appointmentId={}, patientId={}", appointmentId, command.patientId());
        Appointment appointment = findAppointment(appointmentId);
        validateAppointmentIsAvailable(appointment);
        validateAppointmentIsInFuture(appointment);
        Patient patient = findPatient(command.patientId());
        validatePatientAppointmentTimeAvailability(
                patient,
                appointment.getAppointmentStart(),
                appointment.getAppointmentEnd()
        );
        appointment.setPatient(patient);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment booked: appointmentId={}, patientId={}", savedAppointment.getId(), patient.getId());
        return appointmentMapper.toDto(savedAppointment);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        log.info("Deleting appointment: id={}", id);
        appointmentRepository.delete(findAppointment(id));
        log.info("Appointment deleted: id={}", id);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getPatientAppointments(Long patientId) {
        Patient patient = findPatient(patientId);
        return appointmentRepository.findByPatient(patient)
                .stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Appointment not found: id={}", id);
                    return new AppointmentNotFoundException(id);
                });
    }

    private Doctor findDoctor(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    private Patient findPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    private void validateAppointmentTimeAvailability(
            Doctor doctor,
            LocalDateTime appointmentStart,
            LocalDateTime appointmentEnd) {
        if (appointmentRepository
                .existsByDoctorAndAppointmentStartLessThanAndAppointmentEndGreaterThan(
                        doctor,
                        appointmentEnd,
                        appointmentStart)) {
            log.warn("Appointment conflict: doctorId={}, start={}, end={}", doctor.getId(), appointmentStart, appointmentEnd);
            throw new AppointmentAlreadyExistsException();
        }
    }

    private void validateAppointmentIsAvailable(Appointment appointment) {
        if (appointment.getPatient() != null) {
            log.warn("Attempt to book already booked appointment: appointmentId={}, currentPatientId={}", appointment.getId(), appointment.getPatient().getId());
            throw new AppointmentAlreadyBookedException();
        }
    }

    private void validateAppointmentIsInFuture(Appointment appointment) {
        if (appointment.getAppointmentStart().isBefore(LocalDateTime.now())) {
            log.warn("Attempt to book appointment in the past: appointmentId={}, start={}", appointment.getId(), appointment.getAppointmentStart());
            throw new AppointmentDataValidationException(
                    "Cannot book an appointment in the past");
        }
    }

    private void validatePatientAppointmentTimeAvailability(
            Patient patient,
            LocalDateTime appointmentStart,
            LocalDateTime appointmentEnd) {

        if (appointmentRepository
                .existsByPatientAndAppointmentStartLessThanAndAppointmentEndGreaterThan(
                        patient,
                        appointmentEnd,
                        appointmentStart)) {
            log.warn(
                    "Patient appointment conflict: patientId={}, start={}, end={}",
                    patient.getId(),
                    appointmentStart,
                    appointmentEnd
            );
            throw new PatientAppointmentConflictException();
        }
    }
}
