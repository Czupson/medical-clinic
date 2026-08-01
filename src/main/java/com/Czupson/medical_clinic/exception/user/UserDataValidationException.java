package com.Czupson.medical_clinic.exception.user;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class UserDataValidationException extends MedicalClinicException {

    public UserDataValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}