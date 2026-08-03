package com.Czupson.medical_clinic.exception.facility;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class FacilityNotFoundException extends MedicalClinicException {

    public FacilityNotFoundException(String name) {
        super("Facility not found with name: " + name,
                HttpStatus.NOT_FOUND);
    }

    public FacilityNotFoundException(Long id) {
        super("Facility not found with id: " + id,
                HttpStatus.NOT_FOUND);
    }
}