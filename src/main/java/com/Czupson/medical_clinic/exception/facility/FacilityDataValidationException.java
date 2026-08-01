package com.Czupson.medical_clinic.exception.facility;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class FacilityDataValidationException extends MedicalClinicException {

  public FacilityDataValidationException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }
}