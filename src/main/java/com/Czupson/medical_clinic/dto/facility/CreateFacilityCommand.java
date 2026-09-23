package com.Czupson.medical_clinic.dto.facility;

public record CreateFacilityCommand(
    String name,
    String city,
    String postalCode,
    String street,
    String buildingNumber
){
}
