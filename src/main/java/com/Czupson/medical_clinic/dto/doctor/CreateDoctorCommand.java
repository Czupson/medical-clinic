package com.Czupson.medical_clinic.dto.doctor;

public record CreateDoctorCommand(
        Long userId,
        Long facilityId,
        String firstName,
        String lastName,
        String specialization
) {
}