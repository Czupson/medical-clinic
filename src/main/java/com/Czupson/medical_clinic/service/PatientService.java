package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.ChangePasswordCommand;
import com.Czupson.medical_clinic.dto.CreatePatientCommand;
import com.Czupson.medical_clinic.dto.UpdatePatientCommand;
import com.Czupson.medical_clinic.exception.PatientAlreadyExistsException;
import com.Czupson.medical_clinic.exception.PatientNotFoundException;
import com.Czupson.medical_clinic.mapper.PatientMapper;
import com.Czupson.medical_clinic.model.Patient;
import com.Czupson.medical_clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;
    private final PatientMapper mapper;

    public List<Patient> getAllPatients() {
        return repository.findAll();
    }

    public Patient getPatient(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));

    }

    public Patient addPatient(CreatePatientCommand command) {

        if (repository.existsByEmail(command.getEmail())) {
            throw new PatientAlreadyExistsException(command.getEmail());
        }

        Patient patient = mapper.toPatient(command);

        patient.validate();

        return repository.save(patient);
    }

    public Patient updatePatient(String email, UpdatePatientCommand command) {
        Patient patient = repository.findByEmail(email)
                        .orElseThrow(() -> new PatientNotFoundException(email));

        repository.findByEmail(command.getEmail())
                        .ifPresent(foundPatient -> {
                            if (!foundPatient.getEmail().equals(email)) {
                                throw new PatientAlreadyExistsException(command.getEmail());
                            }
                        });

        Patient updatedPatient = mapper.toPatient(command);
        updatedPatient.setId(patient.getId());

        patient.update(updatedPatient);

        return patient;
    }

    public void deletePatient(String email) {
        repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        repository.deleteByEmail(email);

    }

    public void changePassword(String email, ChangePasswordCommand command) {
        Patient patient = repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.changePassword(command.getNewPassword());
    }
}
