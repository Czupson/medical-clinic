package com.Czupson.medical_clinic.dto.doctor;

import java.util.Set;

public record UpdateDoctorCommand(
        Set<Long> facilityIds,
        String firstName,
        String lastName,
        String specialization
) {
}