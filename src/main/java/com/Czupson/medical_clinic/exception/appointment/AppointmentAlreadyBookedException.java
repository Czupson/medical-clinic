package com.Czupson.medical_clinic.exception.appointment;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class AppointmentAlreadyBookedException extends MedicalClinicException {
    public AppointmentAlreadyBookedException() {
        super("Appointment is already booked",
                HttpStatus.CONFLICT);
    }
}