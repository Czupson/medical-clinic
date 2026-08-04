package com.Czupson.medical_clinic.model;

import com.Czupson.medical_clinic.exception.appointment.AppointmentDataValidationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime appointmentStart;

    @Column(nullable = false)
    private LocalDateTime appointmentEnd;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    public void update(Appointment updatedAppointment) {
        updatedAppointment.validate();
        this.appointmentStart = updatedAppointment.getAppointmentStart();
        this.appointmentEnd = updatedAppointment.getAppointmentEnd();
        this.doctor = updatedAppointment.getDoctor();
        this.patient = updatedAppointment.getPatient();
    }

    public void validate() {

        if (appointmentStart == null) {
            throw new AppointmentDataValidationException(
                    "Appointment start cannot be empty");
        }

        if (appointmentEnd == null) {
            throw new AppointmentDataValidationException(
                    "Appointment end cannot be empty");
        }

        if (appointmentStart.isBefore(LocalDateTime.now())) {
            throw new AppointmentDataValidationException(
                    "Appointment cannot start in the past");
        }

        if (!appointmentEnd.isAfter(appointmentStart)) {
            throw new AppointmentDataValidationException(
                    "Appointment end must be after appointment start");
        }

        if (doctor == null) {
            throw new AppointmentDataValidationException(
                    "Appointment must have a doctor");
        }

        validateQuarterHour(appointmentStart, "Appointment start");
        validateQuarterHour(appointmentEnd, "Appointment end");
    }

    private void validateQuarterHour(LocalDateTime dateTime, String fieldName) {
        int minute = dateTime.getMinute();

        if (minute % 15 != 0) {
            throw new AppointmentDataValidationException(
                    fieldName + " must be at 00, 15, 30 or 45 minutes");
        }
        if (dateTime.getSecond() != 0 || dateTime.getNano() != 0) {
            throw new AppointmentDataValidationException(
                    fieldName + " must be an exact quarter hour");
        }
    }

}
