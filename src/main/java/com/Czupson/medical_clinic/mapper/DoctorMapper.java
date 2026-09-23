package com.Czupson.medical_clinic.mapper;

import com.Czupson.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.Czupson.medical_clinic.dto.doctor.DoctorDto;
import com.Czupson.medical_clinic.dto.doctor.UpdateDoctorCommand;
import com.Czupson.medical_clinic.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    Doctor toDoctor(CreateDoctorCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    Doctor toDoctor(UpdateDoctorCommand command);

    DoctorDto toDto(Doctor doctor);
}