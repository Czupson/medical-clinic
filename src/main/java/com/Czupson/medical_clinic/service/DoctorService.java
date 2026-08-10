package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.Czupson.medical_clinic.dto.doctor.DoctorDto;
import com.Czupson.medical_clinic.dto.doctor.UpdateDoctorCommand;
import com.Czupson.medical_clinic.exception.doctor.DoctorAlreadyExistsException;
import com.Czupson.medical_clinic.exception.doctor.DoctorNotFoundException;
import com.Czupson.medical_clinic.exception.facility.FacilitiesNotFoundException;
import com.Czupson.medical_clinic.exception.user.UserNotFoundException;
import com.Czupson.medical_clinic.mapper.DoctorMapper;
import com.Czupson.medical_clinic.model.Doctor;
import com.Czupson.medical_clinic.model.Facility;
import com.Czupson.medical_clinic.model.User;
import com.Czupson.medical_clinic.repository.DoctorRepository;
import com.Czupson.medical_clinic.repository.FacilityRepository;
import com.Czupson.medical_clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final DoctorMapper doctorMapper;

    @Transactional(readOnly = true)
    public PageDto<DoctorDto> getAllDoctors(Pageable pageable) {
        return PageDto.from(
                doctorRepository.findAll(pageable)
                        .map(doctorMapper::toDto)
        );
    }

    @Transactional(readOnly = true)
    public DoctorDto getDoctor(Long id) {
        return doctorMapper.toDto(findDoctor(id));
    }

    @Transactional
    public DoctorDto addDoctor(CreateDoctorCommand command) {
        User user = findUser(command.userId());
        validateDoctorDoesNotExist(user);
        Set<Facility> facilities = findFacilitiesOrThrow(command.facilityIds());
        Doctor doctor = doctorMapper.toDoctor(command);
        doctor.setUser(user);
        doctor.setFacilities(facilities);
        doctor.validate();
        return doctorMapper.toDto(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorDto updateDoctor(Long id, UpdateDoctorCommand command) {
        Doctor doctor = findDoctor(id);
        Set<Facility> facilities = findFacilitiesOrThrow(command.facilityIds());
        Doctor updatedDoctor = doctorMapper.toDoctor(command);
        updatedDoctor.setFacilities(facilities);
        doctor.update(updatedDoctor);
        return doctorMapper.toDto(doctorRepository.save(doctor));
    }

    @Transactional
    public void deleteDoctor(Long id) {
        doctorRepository.delete(findDoctor(id));
    }

    private Doctor findDoctor(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void validateDoctorDoesNotExist(User user) {
        if (doctorRepository.existsByUser(user)) {
            throw new DoctorAlreadyExistsException(user.getId());
        }
    }

    private Set<Facility> findFacilitiesOrThrow(Set<Long> facilityIds) {
        Set<Facility> facilities = new HashSet<>(
                facilityRepository.findAllById(facilityIds)
        );
        if (facilities.size() != facilityIds.size()) {
            throw new FacilitiesNotFoundException();
        }
        return facilities;
    }
}