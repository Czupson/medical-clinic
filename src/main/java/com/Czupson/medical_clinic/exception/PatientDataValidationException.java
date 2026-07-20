package com.Czupson.medical_clinic.exception;

public class PatientDataValidationException extends RuntimeException {

    public PatientDataValidationException(String message) {
        super(message);
    }
}