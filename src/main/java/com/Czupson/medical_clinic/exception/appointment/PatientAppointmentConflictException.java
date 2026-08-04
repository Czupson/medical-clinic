package com.Czupson.medical_clinic.exception.appointment;

import com.Czupson.medical_clinic.exception.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class PatientAppointmentConflictException extends MedicalClinicException {

    public PatientAppointmentConflictException() {
        super(
                "Patient already has an appointment during this time",
                HttpStatus.CONFLICT
        );
    }
}