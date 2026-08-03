package com.Czupson.medical_clinic.exception.appointment;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class AppointmentDataValidationException extends MedicalClinicException {

    public AppointmentDataValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
