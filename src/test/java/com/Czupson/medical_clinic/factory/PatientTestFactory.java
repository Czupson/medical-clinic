package com.Czupson.medical_clinic.factory;

import com.Czupson.medical_clinic.model.Patient;
import com.Czupson.medical_clinic.model.User;

import java.time.LocalDate;

public class PatientTestFactory {

    public static User createUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("jan.kowalski@example.com");
        return user;
    }

    public static Patient createPatient(Long patientId, User user) {
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setIdCardNo("ABC123456");
        patient.setFirstName("Jan");
        patient.setLastName("Kowalski");
        patient.setPhoneNumber("123456789");
        patient.setBirthday(LocalDate.of(1990, 1, 1));
        patient.setUser(user);
        return patient;
    }

    public static Patient createPatient(
            Long id,
            String idCardNo,
            String firstName,
            String lastName,
            String phoneNumber,
            LocalDate birthday,
            User user) {

        Patient patient = new Patient();
        patient.setId(id);
        patient.setIdCardNo(idCardNo);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setPhoneNumber(phoneNumber);
        patient.setBirthday(birthday);
        patient.setUser(user);
        return patient;
    }
}