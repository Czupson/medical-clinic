package com.Czupson.medical_clinic.factory;

import com.Czupson.medical_clinic.model.Facility;

public class FacilityTestFactory {

    public static Facility createFacility(Long facilityId) {
        return createFacility(
                facilityId,
                "Przychodnia Centrum",
                "Warszawa",
                "00-001",
                "Wiejska",
                "1"
        );
    }

    public static Facility createFacility(
            Long id,
            String name,
            String city,
            String postalCode,
            String street,
            String buildingNumber) {

        Facility facility = new Facility();
        facility.setId(id);
        facility.setName(name);
        facility.setCity(city);
        facility.setPostalCode(postalCode);
        facility.setStreet(street);
        facility.setBuildingNumber(buildingNumber);

        return facility;
    }
}