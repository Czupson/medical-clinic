package com.Czupson.medical_clinic.dto.patient;


import java.time.LocalDate;

public record PatientDto(
        Long id,
        String idCardNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}