package com.Czupson.medical_clinic.exception.patient;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends MedicalClinicException {

    public PatientNotFoundException(String email) {
        super("Patient not found with email: " + email,
                HttpStatus.NOT_FOUND);
    }
}