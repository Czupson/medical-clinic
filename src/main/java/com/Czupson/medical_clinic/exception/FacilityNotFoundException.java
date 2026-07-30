package com.Czupson.medical_clinic.exception;

public class FacilityNotFoundException extends RuntimeException {
    public FacilityNotFoundException(Long id) {
        super("Facility not found: " + id);
    }

    public FacilityNotFoundException(String name) {
        super("Facility not found with name: " + name);
    }

    public static FacilityNotFoundException multipleFacilities() {
        return new FacilityNotFoundException("One or more facilities not found");
    }
}