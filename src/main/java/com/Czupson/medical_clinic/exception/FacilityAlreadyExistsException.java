package com.Czupson.medical_clinic.exception;

public class FacilityAlreadyExistsException extends RuntimeException {
    public FacilityAlreadyExistsException(String name) {
        super("Facility already exists: " + name);
    }
}