package com.Czupson.medical_clinic.exception.appointment;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class AppointmentNotFoundException extends MedicalClinicException {
    public AppointmentNotFoundException(Long id) {
        super("Appointment not found with id: " + id,
                HttpStatus.NOT_FOUND);
    }
}
