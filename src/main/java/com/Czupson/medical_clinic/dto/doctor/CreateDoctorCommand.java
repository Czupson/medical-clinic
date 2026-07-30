package com.Czupson.medical_clinic.dto.doctor;

import java.util.Set;

public record CreateDoctorCommand(
        Long userId,
        Set<Long> facilityIds,
        String firstName,
        String lastName,
        String specialization
) {
}