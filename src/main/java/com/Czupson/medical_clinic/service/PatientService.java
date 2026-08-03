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

    public Patient getPatient(Long id) {
        return findPatient(id);
    }

    public Patient addPatient(CreatePatientCommand command) {
        User user = findUser(command.userId());
        validatePatientDoesNotExist(user);
        Patient patient = mapper.toPatient(command);
        patient.setUser(user);
        patient.validate();
        return repository.save(patient);
    }

    public Patient updatePatient(Long id, UpdatePatientCommand command) {
        Patient patient = findPatient(id);
        Patient updatedPatient = mapper.toPatient(command);
        updatedPatient.setId(patient.getId());
        updatedPatient.setUser(patient.getUser());
        patient.update(updatedPatient);
        return repository.save(patient);
    }

    public void deletePatient(Long id) {
        repository.delete(findPatient(id));
    }

    private Patient findPatient(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private void validatePatientDoesNotExist(User user) {
        if (user.getPatient() != null) {
            throw new PatientAlreadyExistsException(user.getEmail());
        }
    }
}
