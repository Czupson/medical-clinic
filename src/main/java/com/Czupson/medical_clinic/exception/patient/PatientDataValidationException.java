package com.Czupson.medical_clinic.exception.patient;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class PatientDataValidationException extends MedicalClinicException {
    public PatientDataValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}