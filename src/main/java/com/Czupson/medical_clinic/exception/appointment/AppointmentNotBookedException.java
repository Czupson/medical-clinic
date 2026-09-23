package com.Czupson.medical_clinic.exception.appointment;

public class AppointmentNotBookedException extends RuntimeException {
    public AppointmentNotBookedException(Long appointmentId) {
        super("Appointment with id " + appointmentId + " is not booked");
    }
}