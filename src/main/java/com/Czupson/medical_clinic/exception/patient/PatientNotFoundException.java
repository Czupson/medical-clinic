package com.Czupson.medical_clinic.exception.patient;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends MedicalClinicException {

    public PatientNotFoundException(Long id) {
        super("Patient not found with id: " + id,
                HttpStatus.NOT_FOUND);
    }
}