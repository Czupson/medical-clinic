package com.Czupson.medical_clinic.exception.facility;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class FacilityNotFoundException extends MedicalClinicException {

    public FacilityNotFoundException(Long id) {
        super("Facility not found with id: " + id,
                HttpStatus.NOT_FOUND);
    }
}