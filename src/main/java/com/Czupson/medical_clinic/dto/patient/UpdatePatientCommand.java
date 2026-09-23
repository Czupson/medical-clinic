package com.Czupson.medical_clinic.dto.patient;

import java.time.LocalDate;

public record UpdatePatientCommand(
        Long userId,
        String idCardNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}
