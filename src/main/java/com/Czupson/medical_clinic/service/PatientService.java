package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.patient.CreatePatientCommand;
import com.Czupson.medical_clinic.dto.patient.PatientDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;
    private final PatientMapper mapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageDto<PatientDto> getAllPatients(Pageable pageable) {
        return PageDto.from(
                repository.findAll(pageable)
                        .map(mapper::toDto)
        );
    }

    @Transactional(readOnly = true)
    public PatientDto getPatient(Long id) {
        return mapper.toDto(findPatient(id));
    }

    @Transactional
    public PatientDto addPatient(CreatePatientCommand command) {
        log.info("Creating patient for userId={}", command.userId());
        User user = findUser(command.userId());
        validatePatientDoesNotExist(user);
        Patient patient = mapper.toPatient(command);
        patient.setUser(user);
        patient.validate();
        Patient savedPatient = repository.save(patient);
        log.info("Patient created: id={}, userId={}", savedPatient.getId(), user.getId());
        return mapper.toDto(savedPatient);
    }

    @Transactional
    public PatientDto updatePatient(Long id, UpdatePatientCommand command) {
        log.info("Updating patient: id={}", id);
        Patient patient = findPatient(id);
        Patient updatedPatient = mapper.toPatient(command);
        updatedPatient.setId(patient.getId());
        updatedPatient.setUser(patient.getUser());
        patient.update(updatedPatient);
        Patient savedPatient = repository.save(patient);
        log.info("Patient updated: id={}", savedPatient.getId());
        return mapper.toDto(savedPatient);
    }

    @Transactional
    public void deletePatient(Long id) {
        log.info("Deleting patient: id={}", id);
        repository.delete(findPatient(id));
        log.info("Patient deleted: id={}", id);
    }

    private Patient findPatient(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Patient not found: id={}", id);
                    return new PatientNotFoundException(id);
                });
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found while creating patient: userId={}", id);
                    return new UserNotFoundException(id);
                });
    }

    private void validatePatientDoesNotExist(User user) {
        if (user.getPatient() != null) {
            log.warn("Attempt to create another patient for userId={}", user.getId());
            throw new PatientAlreadyExistsException(user.getEmail());
        }
    }
}
