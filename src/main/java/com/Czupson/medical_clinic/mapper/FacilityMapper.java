package com.Czupson.medical_clinic.mapper;

import com.Czupson.medical_clinic.dto.facility.CreateFacilityCommand;
import com.Czupson.medical_clinic.dto.facility.FacilityDto;
import com.Czupson.medical_clinic.dto.facility.UpdateFacilityCommand;
import com.Czupson.medical_clinic.model.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FacilityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    Facility toFacility(CreateFacilityCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    Facility toFacility(UpdateFacilityCommand command);

    FacilityDto toDto(Facility facility);
}
