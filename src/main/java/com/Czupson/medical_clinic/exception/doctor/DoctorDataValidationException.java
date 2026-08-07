package com.Czupson.medical_clinic.exception.doctor;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class DoctorDataValidationException extends MedicalClinicException {
    public DoctorDataValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}