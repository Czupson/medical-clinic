package com.Czupson.medical_clinic.dto.appointment;

import java.time.LocalDateTime;

public record AppointmentDto(
        Long id,
        LocalDateTime appointmentStart,
        LocalDateTime appointmentEnd,
        Long doctorId,
        Long patientId
) {
}