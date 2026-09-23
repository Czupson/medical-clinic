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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static com.Czupson.medical_clinic.factory.FacilityTestFactory.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class FacilityServiceTest {
    private FacilityService facilityService;
    private FacilityRepository facilityRepository;
    private FacilityMapper facilityMapper;

    @BeforeEach
    void setUp() {
        facilityRepository = mock(FacilityRepository.class);
        facilityMapper = mock(FacilityMapper.class);
        facilityService = new FacilityService(facilityRepository, facilityMapper);
    }

    @Test
    void getFacility_FacilityExists_FacilityReturned() {
        // given
        Long facilityId = 1L;
        Facility facility = createFacility(facilityId);
        FacilityDto facilityDto = new FacilityDto(facilityId, "Przychodnia Centrum", "Warszawa", "00-001",
                "Wiejska", "1");
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));
        when(facilityMapper.toDto(facility)).thenReturn(facilityDto);
        // when
        FacilityDto result = facilityService.getFacility(facilityId);
        // then
        assertEquals(facilityDto, result);
        verify(facilityRepository).findById(facilityId);
        verify(facilityMapper).toDto(facility);
    }

    @Test
    void getAllFacilities_FacilitiesExist_FacilitiesReturned() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Facility facility = createFacility(1L);
        FacilityDto facilityDto = new FacilityDto(1L, "Przychodnia Centrum", "Warszawa", "00-001",
                "Wiejska", "1");
        Page<Facility> facilityPage = new PageImpl<>(List.of(facility), pageable, 1);
        when(facilityRepository.findAll(pageable)).thenReturn(facilityPage);
        when(facilityMapper.toDto(facility)).thenReturn(facilityDto);
        // when
        PageDto<FacilityDto> result = facilityService.getAllFacilities(pageable);
        // then
        assertEquals(List.of(facilityDto), result.content());
        assertEquals(0, result.pageNumber());
        assertEquals(10, result.pageSize());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        verify(facilityRepository).findAll(pageable);
        verify(facilityMapper).toDto(facility);
    }

    @Test
    void addFacility_ValidCommand_FacilityCreated() {
        // given
        CreateFacilityCommand command = new CreateFacilityCommand("Przychodnia Centrum", "Warszawa",
                "00-001", "Wiejska", "1");
        Facility facility = createFacility(1L);
        FacilityDto facilityDto = new FacilityDto(1L, "Przychodnia Centrum", "Warszawa", "00-001",
                "Wiejska", "1");
        when(facilityRepository.existsByName(command.name())).thenReturn(false);
        when(facilityMapper.toFacility(command)).thenReturn(facility);
        when(facilityRepository.save(facility)).thenReturn(facility);
        when(facilityMapper.toDto(facility)).thenReturn(facilityDto);
        // when
        FacilityDto result = facilityService.addFacility(command);
        // then
        assertEquals(facilityDto, result);
        verify(facilityRepository).existsByName(command.name());
        verify(facilityMapper).toFacility(command);
        verify(facilityRepository).save(facility);
        verify(facilityMapper).toDto(facility);
    }

    @Test
    void addFacility_FacilityAlreadyExists_ExceptionThrown() {
        // given
        CreateFacilityCommand command = new CreateFacilityCommand("Przychodnia Centrum", "Warszawa",
                "00-001", "Wiejska", "1");
        when(facilityRepository.existsByName(command.name())).thenReturn(true);
        // when and then
        assertThrows(FacilityAlreadyExistsException.class, () -> facilityService.addFacility(command));
        verify(facilityRepository).existsByName(command.name());
        verifyNoInteractions(facilityMapper);
        verify(facilityRepository, never()).save(any(Facility.class));
    }

    @Test
    void updateFacility_ValidCommand_FacilityUpdated() {
        // given
        Long facilityId = 1L;
        Facility facility = createFacility(facilityId);
        UpdateFacilityCommand command = new UpdateFacilityCommand("Przychodnia Młynowa", "Białystok",
                "15-404", "Młynowa", "17");
        Facility updatedFacility = createFacility(facilityId, "Przychodnia Młynowa", "Białystok", "15-404", "Młynowa", "17");
        FacilityDto facilityDto = new FacilityDto(facilityId, "Przychodnia Młynowa", "Białystok",
                "15-404", "Młynowa", "17");
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));
        when(facilityRepository.findByName(command.name())).thenReturn(Optional.empty());
        when(facilityMapper.toFacility(command)).thenReturn(updatedFacility);
        when(facilityRepository.save(facility)).thenReturn(facility);
        when(facilityMapper.toDto(facility)).thenReturn(facilityDto);
        // when
        FacilityDto result = facilityService.updateFacility(facilityId, command);
        // then
        assertEquals(facilityDto, result);
        verify(facilityRepository).findById(facilityId);
        verify(facilityRepository).findByName(command.name());
        verify(facilityMapper).toFacility(command);
        verify(facilityRepository).save(facility);
        verify(facilityMapper).toDto(facility);
    }

    @Test
    void updateFacility_NameAlreadyExists_ExceptionThrown() {
        // given
        Long facilityId = 1L;
        Facility facility = createFacility(facilityId);
        UpdateFacilityCommand command = new UpdateFacilityCommand("Przychodnia Młynowa", "Białystok",
                "15-404", "Młynowa", "17");
        Facility existingFacility = createFacility(2L, "Przychodnia Młynowa", "Białystok", "15-404",
                "Młynowa", "17");
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));
        when(facilityRepository.findByName(command.name())).thenReturn(Optional.of(existingFacility));
        // when and then
        assertThrows(FacilityAlreadyExistsException.class, () -> facilityService.updateFacility(facilityId, command));
        verify(facilityRepository).findById(facilityId);
        verify(facilityRepository).findByName(command.name());
        verifyNoInteractions(facilityMapper);
        verify(facilityRepository, never()).save(any(Facility.class));
    }

    @Test
    void getFacility_FacilityDoesNotExist_ExceptionThrown() {
        // given
        Long facilityId = 1L;
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(FacilityNotFoundException.class, () -> facilityService.getFacility(facilityId));
        verify(facilityRepository).findById(facilityId);
        verifyNoInteractions(facilityMapper);
    }

    @Test
    void updateFacility_FacilityDoesNotExist_ExceptionThrown() {
        // given
        Long facilityId = 1L;
        UpdateFacilityCommand command = new UpdateFacilityCommand("Przychodnia Młynowa", "Białystok",
                "15-404", "Młynowa", "17");
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(FacilityNotFoundException.class, () -> facilityService.updateFacility(facilityId, command));
        verify(facilityRepository).findById(facilityId);
        verify(facilityRepository, never()).findByName(anyString());
        verify(facilityRepository, never()).save(any(Facility.class));
        verifyNoInteractions(facilityMapper);
    }

    @Test
    void deleteFacility_FacilityExists_FacilityDeleted() {
        // given
        Long facilityId = 1L;
        Facility facility = createFacility(facilityId);
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));
        // when
        facilityService.deleteFacility(facilityId);
        // then
        verify(facilityRepository).findById(facilityId);
        verify(facilityRepository).delete(facility);
    }

    @Test
    void deleteFacility_FacilityDoesNotExist_ExceptionThrown() {
        // given
        Long facilityId = 1L;
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(FacilityNotFoundException.class, () -> facilityService.deleteFacility(facilityId));
        verify(facilityRepository).findById(facilityId);
        verify(facilityRepository, never()).delete(any(Facility.class));
    }
}
