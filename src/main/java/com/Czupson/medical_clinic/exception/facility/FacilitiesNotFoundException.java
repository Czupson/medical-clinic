package com.Czupson.medical_clinic.exception.facility;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class FacilitiesNotFoundException extends MedicalClinicException {

    public FacilitiesNotFoundException() {
        super("One or more facilities do not exist",
                HttpStatus.NOT_FOUND);
    }
}