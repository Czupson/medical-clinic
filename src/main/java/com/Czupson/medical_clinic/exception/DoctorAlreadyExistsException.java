package com.Czupson.medical_clinic.exception;

public class DoctorAlreadyExistsException extends RuntimeException {
  public DoctorAlreadyExistsException(Long userId) {
    super("Doctor already exists for user with id: " + userId);
  }
}