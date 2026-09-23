package com.Czupson.medical_clinic.dto.patient;

import java.time.LocalDate;

public record CreatePatientCommand(
        Long userId,
        String idCardNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}
