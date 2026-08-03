package com.Czupson.medical_clinic.mapper;

import com.Czupson.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.Czupson.medical_clinic.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    Appointment toAppointment(CreateAppointmentCommand command);
}
