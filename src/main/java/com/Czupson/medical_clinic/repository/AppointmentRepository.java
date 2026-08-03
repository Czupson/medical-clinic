package com.Czupson.medical_clinic.repository;

import com.Czupson.medical_clinic.model.Appointment;
import com.Czupson.medical_clinic.model.Doctor;
import com.Czupson.medical_clinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorAndAppointmentStartLessThanAndAppointmentEndGreaterThan(
            Doctor doctor,
            LocalDateTime appointmentEnd,
            LocalDateTime appointmentStart
    );
    List<Appointment> findByPatient(Patient patient);
}
