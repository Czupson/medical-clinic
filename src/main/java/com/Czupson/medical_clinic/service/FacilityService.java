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

    public Facility getFacility(Long id) {
        return findFacility(id);
    }

    public Facility addFacility(CreateFacilityCommand command) {
        validateFacilityDoesNotExist(command.name());
        Facility facility = facilityMapper.toFacility(command);
        facility.validate();
        return facilityRepository.save(facility);
    }

    public Facility updateFacility(Long id, UpdateFacilityCommand command) {
        Facility facility = findFacility(id);
        validateFacilityNameUniqueness(id, command.name());
        Facility updatedFacility = facilityMapper.toFacility(command);
        facility.update(updatedFacility);
        return facilityRepository.save(facility);
    }

    public void deleteFacility(Long id) {
        facilityRepository.delete(findFacility(id));
    }

    private Facility findFacility(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityNotFoundException(id));
    }

    private void validateFacilityDoesNotExist(String name) {
        if (facilityRepository.existsByName(name)) {
            throw new FacilityAlreadyExistsException(name);
        }
    }

    private void validateFacilityNameUniqueness(Long facilityId, String name) {
        facilityRepository.findByName(name)
                .ifPresent(foundFacility -> {
                    if (!foundFacility.getId().equals(facilityId)) {
                        throw new FacilityAlreadyExistsException(name);
                    }
                });
    }
}
