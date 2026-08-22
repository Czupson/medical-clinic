package com.Czupson.medical_clinic.exception.appointment;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class AppointmentAlreadyExistsException extends MedicalClinicException {
    public AppointmentAlreadyExistsException() {
        super("Doctor already has an appointment at this time",
                HttpStatus.CONFLICT);
    }
}
