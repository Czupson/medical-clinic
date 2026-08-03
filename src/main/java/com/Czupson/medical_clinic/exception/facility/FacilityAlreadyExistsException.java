package com.Czupson.medical_clinic.exception.facility;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class FacilityAlreadyExistsException extends MedicalClinicException {

    public FacilityAlreadyExistsException(String name) {
        super("Facility already exists with name: " + name,
                HttpStatus.CONFLICT);
    }
}