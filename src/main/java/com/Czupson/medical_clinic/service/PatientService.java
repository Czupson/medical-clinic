package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.patient.CreatePatientCommand;
import com.Czupson.medical_clinic.dto.patient.UpdatePatientCommand;
import com.Czupson.medical_clinic.exception.patient.PatientAlreadyExistsException;
import com.Czupson.medical_clinic.exception.patient.PatientNotFoundException;
import com.Czupson.medical_clinic.exception.user.UserNotFoundException;
import com.Czupson.medical_clinic.mapper.PatientMapper;
import com.Czupson.medical_clinic.model.Patient;
import com.Czupson.medical_clinic.model.User;
import com.Czupson.medical_clinic.repository.PatientRepository;
import com.Czupson.medical_clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;
    private final PatientMapper mapper;
    private final UserRepository userRepository;

    public List<Patient> getAllPatients() {
        return repository.findAll();
    }

    public Patient getPatient(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        Patient patient = user.getPatient();
        if (patient == null) {
            throw new PatientNotFoundException(email);
        }
        return patient;
    }

    public Patient addPatient(CreatePatientCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));
        if (user.getPatient() != null) {
            throw new PatientAlreadyExistsException(user.getEmail());
        }
        Patient patient = mapper.toPatient(command);
        patient.setUser(user);
        patient.validate();
        return repository.save(patient);
    }

    public Patient updatePatient(String email, UpdatePatientCommand command) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        Patient patient = user.getPatient();
        if (patient == null) {
            throw new PatientNotFoundException(email);
        }
        Patient updatedPatient = mapper.toPatient(command);
        updatedPatient.setId(patient.getId());
        updatedPatient.setUser(user);
        patient.update(updatedPatient);
        return repository.save(patient);
    }

    public void deletePatient(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        Patient patient = user.getPatient();
        if (patient == null) {
            throw new PatientNotFoundException(email);
        }
        repository.delete(patient);
    }
}
