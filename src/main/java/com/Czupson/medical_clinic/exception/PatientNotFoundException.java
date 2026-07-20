package com.Czupson.medical_clinic.exception;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(String email) {
        super("Patient with email '" + email + "' not found.");
    }
}