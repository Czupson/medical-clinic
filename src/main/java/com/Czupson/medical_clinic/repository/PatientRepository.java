package com.Czupson.medical_clinic.repository;

import com.Czupson.medical_clinic.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PatientRepository {

    private final List<Patient> patients = new ArrayList<>();

    public List<Patient> findAll() {
        return patients;
    }

    public Patient findByEmail(String email) {
        for (Patient patient : patients) {
            if (patient.getEmail().equals(email)) {
                return patient;
            }
        }
        return null;
    }

    public boolean existsByEmail(String email) {
        return patients.stream()
                .anyMatch(patient -> patient.getEmail().equals(email));
    }

    public Patient save(Patient patient) {
        patients.add(patient);
        return patient;
    }

    public void deleteByEmail(String email) {
        patients.removeIf(patient -> patient.getEmail().equals(email));
    }
}