package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.Czupson.medical_clinic.dto.doctor.UpdateDoctorCommand;
import com.Czupson.medical_clinic.exception.DoctorAlreadyExistsException;
import com.Czupson.medical_clinic.exception.DoctorNotFoundException;
import com.Czupson.medical_clinic.exception.FacilityNotFoundException;
import com.Czupson.medical_clinic.exception.UserNotFoundException;
import com.Czupson.medical_clinic.mapper.DoctorMapper;
import com.Czupson.medical_clinic.model.Doctor;
import com.Czupson.medical_clinic.model.Facility;
import com.Czupson.medical_clinic.model.User;
import com.Czupson.medical_clinic.repository.DoctorRepository;
import com.Czupson.medical_clinic.repository.FacilityRepository;
import com.Czupson.medical_clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final DoctorMapper doctorMapper;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    public Doctor getDoctor(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    public Doctor addDoctor(CreateDoctorCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));
        if (doctorRepository.existsByUser(user)) {
            throw new DoctorAlreadyExistsException(user.getId());
        }
        Facility facility = facilityRepository.findById(command.facilityId())
                .orElseThrow(() -> new FacilityNotFoundException(command.facilityId()));
        Doctor doctor = doctorMapper.toDoctor(command);
        doctor.setUser(user);
        doctor.setFacility(facility);
        doctor.validate();
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(Long id, UpdateDoctorCommand command) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        Facility facility = facilityRepository.findById(command.facilityId())
                .orElseThrow(() -> new FacilityNotFoundException(command.facilityId()));
        Doctor updatedDoctor = doctorMapper.toDoctor(command);
        updatedDoctor.setFacility(facility);
        doctor.update(updatedDoctor);
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        doctorRepository.delete(doctor);
    }
}