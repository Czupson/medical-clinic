package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.facility.CreateFacilityCommand;
import com.Czupson.medical_clinic.dto.facility.UpdateFacilityCommand;
import com.Czupson.medical_clinic.exception.facility.FacilityAlreadyExistsException;
import com.Czupson.medical_clinic.exception.facility.FacilityNotFoundException;
import com.Czupson.medical_clinic.mapper.FacilityMapper;
import com.Czupson.medical_clinic.model.Facility;
import com.Czupson.medical_clinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public Facility getFacility(String name) {
        return facilityRepository.findByName(name)
                .orElseThrow(() -> new FacilityNotFoundException(name));
    }

    public Facility addFacility(CreateFacilityCommand command) {
        if (facilityRepository.existsByName(command.name())) {
            throw new FacilityAlreadyExistsException(command.name());
        }
        Facility facility = facilityMapper.toFacility(command);
        facility.validate();
        return facilityRepository.save(facility);
    }

    public Facility updateFacility(String name, UpdateFacilityCommand command) {
        Facility facility = facilityRepository.findByName(name)
                .orElseThrow(() -> new FacilityNotFoundException(name));
        if (!facility.getName().equals(command.name())
                && facilityRepository.existsByName(command.name())) {
            throw new FacilityAlreadyExistsException(command.name());
        }
        Facility updatedFacility = facilityMapper.toFacility(command);
        facility.update(updatedFacility);
        return facilityRepository.save(facility);
    }

    public void deleteFacility(String name) {
        Facility facility = facilityRepository.findByName(name)
                .orElseThrow(() -> new FacilityNotFoundException(name));
        facilityRepository.delete(facility);
    }
}
