package com.Czupson.medical_clinic.exception.user;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends MedicalClinicException {

    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email,
                HttpStatus.CONFLICT);
    }
}