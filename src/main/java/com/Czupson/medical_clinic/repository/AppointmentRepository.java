package com.Czupson.medical_clinic.repository;

import com.Czupson.medical_clinic.model.Appointment;
import com.Czupson.medical_clinic.model.Doctor;
import com.Czupson.medical_clinic.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorAndAppointmentStartLessThanAndAppointmentEndGreaterThan(
            Doctor doctor,
            LocalDateTime appointmentEnd,
            LocalDateTime appointmentStart
    );

    boolean existsByPatientAndAppointmentStartLessThanAndAppointmentEndGreaterThan(
            Patient patient,
            LocalDateTime appointmentEnd,
            LocalDateTime appointmentStart
    );

    Page<Appointment> findByPatient(Patient patient, Pageable pageable);

    Page<Appointment> findByDoctorAndPatientIsNull(Doctor doctor, Pageable pageable);

    @Query("""
    SELECT a
    FROM Appointment a
    WHERE a.patient IS NULL
      AND a.doctor.specialization = :specialization
      AND a.appointmentStart >= :start
      AND a.appointmentStart < :end
    """)
    Page<Appointment> findAvailableAppointments(
            @Param("specialization") String specialization,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );
}
