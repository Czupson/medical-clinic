package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.appointment.BookAppointmentCommand;
import com.Czupson.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.Czupson.medical_clinic.exception.appointment.AppointmentAlreadyBookedException;
import com.Czupson.medical_clinic.exception.appointment.AppointmentAlreadyExistsException;
import com.Czupson.medical_clinic.exception.appointment.AppointmentDataValidationException;
import com.Czupson.medical_clinic.exception.appointment.AppointmentNotFoundException;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointment(Long id) {
        return findAppointment(id);
    }

    public Appointment addAppointment(CreateAppointmentCommand command) {
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
        return appointmentRepository.save(appointment);
    }

    public Appointment bookAppointment(Long appointmentId,
                                       BookAppointmentCommand command) {

        Appointment appointment = findAppointment(appointmentId);
        validateAppointmentIsAvailable(appointment);
        validateAppointmentIsInFuture(appointment);
        Patient patient = findPatient(command.patientId());
        appointment.setPatient(patient);
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.delete(findAppointment(id));
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        Patient patient = findPatient(patientId);
        return appointmentRepository.findByPatient(patient);
    }

    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
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
            throw new AppointmentAlreadyExistsException();
        }
    }

    private void validateAppointmentIsAvailable(Appointment appointment) {
        if (appointment.getPatient() != null) {
            throw new AppointmentAlreadyBookedException();
        }
    }

    private void validateAppointmentIsInFuture(Appointment appointment) {
        if (appointment.getAppointmentStart().isBefore(LocalDateTime.now())) {
            throw new AppointmentDataValidationException(
                    "Cannot book an appointment in the past");
        }
    }
}
