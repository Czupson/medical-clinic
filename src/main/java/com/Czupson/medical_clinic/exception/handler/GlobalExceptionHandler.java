package com.Czupson.medical_clinic.exception.handler;

import com.Czupson.medical_clinic.exception.appointment.AppointmentNotBookedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MedicalClinicException.class)
    public ResponseEntity<ErrorResponse> handleMedicalClinicException(
            MedicalClinicException exception) {
        ErrorResponse response = new ErrorResponse(
                exception.getStatus().value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }

    @ExceptionHandler(AppointmentNotBookedException.class)
    public ResponseEntity<ErrorResponse> handleAppointmentNotBooked(
            AppointmentNotBookedException exception) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}