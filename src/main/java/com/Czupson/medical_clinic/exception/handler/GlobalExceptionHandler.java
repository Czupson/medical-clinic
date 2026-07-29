package com.Czupson.medical_clinic.exception.handler;

import com.Czupson.medical_clinic.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFound(
            PatientNotFoundException exception) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PatientAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePatientAlreadyExists(
            PatientAlreadyExistsException exception) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(PatientDataValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            PatientDataValidationException exception) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException exception) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(UserDataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserDataValidationException(UserDataValidationException exception) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(FacilityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleFacilityNotFoundException(FacilityNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(FacilityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleFacilityAlreadyExistsException(FacilityAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(DoctorAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDoctorAlreadyExistsException(DoctorAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleDoctorNotFoundException(DoctorNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(DoctorDataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDoctorDataValidationException(DoctorDataValidationException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(FacilityDataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleFacilityDataValidationException(FacilityDataValidationException ex) {
        return ex.getMessage();
    }
}