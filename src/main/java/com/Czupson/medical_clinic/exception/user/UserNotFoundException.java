package com.Czupson.medical_clinic.exception.user;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends MedicalClinicException {

    public UserNotFoundException(Long id) {
        super("User not found with id: " + id,
                HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email,
                HttpStatus.NOT_FOUND);
    }
}
