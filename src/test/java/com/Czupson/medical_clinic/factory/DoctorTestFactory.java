package com.Czupson.medical_clinic.factory;

import com.Czupson.medical_clinic.model.Doctor;
import com.Czupson.medical_clinic.model.Facility;
import com.Czupson.medical_clinic.model.User;

import java.util.HashSet;
import java.util.Set;

public class DoctorTestFactory {

    public static Doctor createDoctor(Long doctorId) {
        return createDoctor(
                doctorId,
                "Jan",
                "Kowalski",
                "Kardiolog",
                createUser(1L),
                Set.of(createFacility(1L))
        );
    }

    public static User createUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("jan.kowalski@example.com");
        user.setPassword("password123");
        return user;
    }

    public static Facility createFacility(Long facilityId) {
        Facility facility = new Facility();
        facility.setId(facilityId);
        facility.setName("Przychodnia Młynowa");
        facility.setCity("Białystok");
        facility.setPostalCode("15-404");
        facility.setStreet("Młynowa");
        facility.setBuildingNumber("17");
        return facility;
    }

    public static Doctor createDoctor(
            Long doctorId,
            String firstName,
            String lastName,
            String specialization,
            User user,
            Set<Facility> facilities) {

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setSpecialization(specialization);
        doctor.setUser(user);
        doctor.setFacilities(new HashSet<>(facilities));

        return doctor;
    }
}