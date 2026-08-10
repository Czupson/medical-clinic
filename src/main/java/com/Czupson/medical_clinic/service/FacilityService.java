package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.facility.CreateFacilityCommand;
import com.Czupson.medical_clinic.dto.facility.FacilityDto;
import com.Czupson.medical_clinic.dto.facility.UpdateFacilityCommand;
import com.Czupson.medical_clinic.exception.facility.FacilityAlreadyExistsException;
import com.Czupson.medical_clinic.exception.facility.FacilityNotFoundException;
import com.Czupson.medical_clinic.mapper.FacilityMapper;
import com.Czupson.medical_clinic.model.Facility;
import com.Czupson.medical_clinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    @Transactional(readOnly = true)
    public PageDto<FacilityDto> getAllFacilities(Pageable pageable) {
        return PageDto.from(
                facilityRepository.findAll(pageable)
                        .map(facilityMapper::toDto)
        );
    }

    @Transactional(readOnly = true)
    public FacilityDto getFacility(Long id) {
        return facilityMapper.toDto(findFacility(id));
    }

    @Transactional
    public FacilityDto addFacility(CreateFacilityCommand command) {
        validateFacilityDoesNotExist(command.name());
        Facility facility = facilityMapper.toFacility(command);
        facility.validate();
        return facilityMapper.toDto(facilityRepository.save(facility));
    }

    @Transactional
    public FacilityDto updateFacility(Long id, UpdateFacilityCommand command) {
        Facility facility = findFacility(id);
        validateFacilityNameUniqueness(id, command.name());
        Facility updatedFacility = facilityMapper.toFacility(command);
        facility.update(updatedFacility);
        return facilityMapper.toDto(facilityRepository.save(facility));
    }

    @Transactional
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
