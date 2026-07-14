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
        Patient patient = repository.findByEmail(email);
        if (patient == null) {
            throw new PatientNotFoundException(email);
        }
        return patient;
    }
    public Patient addPatient(Patient patient) {

        if (repository.existsByEmail(patient.getEmail())) {
            throw new PatientAlreadyExistsException(patient.getEmail());
        }

        return repository.save(patient);
    }
    public Patient updatePatient(String email, Patient updatedPatient) {

        Patient patient = repository.findByEmail(email);

        if (patient == null) {
            throw new PatientNotFoundException(email);
        }

        patient.setFirstName(updatedPatient.getFirstName());
        patient.setLastName(updatedPatient.getLastName());
        patient.setPhoneNumber(updatedPatient.getPhoneNumber());
        patient.setBirthday(updatedPatient.getBirthday());
        patient.setIdCardNo(updatedPatient.getIdCardNo());
        patient.setPassword(updatedPatient.getPassword());
        patient.setEmail(updatedPatient.getEmail());

        return repository.save(patient);
    }
    public void deletePatient(String email) {

        Patient patient = repository.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("Patient not found");
        }

        repository.deleteByEmail(email);
    }
}
