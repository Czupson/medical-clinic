package com.Czupson.medical_clinic.exception.doctor;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class DoctorAlreadyExistsException extends MedicalClinicException {
  public DoctorAlreadyExistsException(Long userId) {
    super("Doctor already exists for user with id: " + userId,
            HttpStatus.CONFLICT);
  }
}