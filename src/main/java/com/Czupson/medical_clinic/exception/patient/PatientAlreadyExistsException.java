package com.Czupson.medical_clinic.exception.patient;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class PatientAlreadyExistsException extends MedicalClinicException {
    public PatientAlreadyExistsException(String email) {
        super("Patient already exists with email: " + email,
                HttpStatus.CONFLICT);
    }
}