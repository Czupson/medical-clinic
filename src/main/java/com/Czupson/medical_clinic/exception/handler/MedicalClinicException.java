package com.Czupson.medical_clinic.exception.handler;

import org.springframework.http.HttpStatus;

public abstract class MedicalClinicException extends RuntimeException {
    private final HttpStatus status;

    protected MedicalClinicException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}