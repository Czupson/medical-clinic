package com.Czupson.medical_clinic.dto.facility;

public record FacilityDto(
        Long id,
        String name,
        String city,
        String postalCode,
        String street,
        String buildingNumber
) {
}