package com.Czupson.medical_clinic.dto.doctor;

public record DoctorDto(
        Long id,
        String firstName,
        String lastName,
        String specialization
) {
}