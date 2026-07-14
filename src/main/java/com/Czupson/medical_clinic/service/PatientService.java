package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.exception.PatientAlreadyExistsException;
import com.Czupson.medical_clinic.exception.PatientNotFoundException;
import com.Czupson.medical_clinic.model.Patient;
import com.Czupson.medical_clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;

    public List<Patient> getAllPatients() {
        return repository.findAll();
    }

    public Patient getPatient(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));

    }

    public Patient addPatient(Patient patient) {
        if (repository.existsByEmail(patient.getEmail())) {
            throw new PatientAlreadyExistsException(patient.getEmail());
        }

        return repository.save(patient);
    }

    public Patient updatePatient(String email, Patient updatedPatient) {
        Patient patient = repository.findByEmail(email)
                        .orElseThrow(() -> new PatientNotFoundException(email));

        repository.findByEmail(updatedPatient.getEmail())
                        .ifPresent(foundPatient -> {
                            if (!foundPatient.getEmail().equals(email)) {
                                throw new PatientAlreadyExistsException(updatedPatient.getEmail());
                            }
                        });
        patient.update(updatedPatient);

        return patient;
    }

    public void deletePatient(String email) {
        repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        repository.deleteByEmail(email);

    }

    public void changePassword(String email, String newPassword) {
        Patient patient = repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.setPassword(newPassword);
    }

}
