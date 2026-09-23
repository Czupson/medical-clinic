package com.Czupson.medical_clinic.dto.appointment;

import java.time.LocalDateTime;

public record CreateAppointmentCommand(
        Long doctorId,
        LocalDateTime appointmentStart,
        LocalDateTime appointmentEnd
) {
}
