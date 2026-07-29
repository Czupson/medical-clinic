package com.Czupson.medical_clinic.dto.doctor;

public record UpdateDoctorCommand(
        Long facilityId,
        String firstName,
        String lastName,
        String specialization
) {
}