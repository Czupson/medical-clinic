package com.Czupson.medical_clinic.exception.doctor;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException(Long id) {
        super("Doctor not found with id: " + id,
                HttpStatus.NOT_FOUND);
    }
}