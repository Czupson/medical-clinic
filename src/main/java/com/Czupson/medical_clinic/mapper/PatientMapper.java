package com.Czupson.medical_clinic.mapper;

import com.Czupson.medical_clinic.dto.CreatePatientCommand;
import com.Czupson.medical_clinic.dto.UpdatePatientCommand;
import com.Czupson.medical_clinic.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(target = "id", ignore = true)
    Patient toPatient(CreatePatientCommand command);

    @Mapping(target = "id", ignore = true)
    Patient toPatient(UpdatePatientCommand command);
}