package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.facility.CreateFacilityCommand;
import com.Czupson.medical_clinic.dto.facility.UpdateFacilityCommand;
import com.Czupson.medical_clinic.model.Facility;
import com.Czupson.medical_clinic.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    public Page<Facility> getAllFacilities(Pageable pageable) {
        return facilityService.getAllFacilities(pageable);
    }

    @GetMapping("/{id}")
    public Facility getFacility(@PathVariable Long id) {
        return facilityService.getFacility(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Facility addFacility(@RequestBody CreateFacilityCommand command) {
        return facilityService.addFacility(command);
    }

    @PutMapping("/{id}")
    public Facility updateFacility(@PathVariable Long id,
                                   @RequestBody UpdateFacilityCommand command) {
        return facilityService.updateFacility(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFacility(@PathVariable Long id) {
        facilityService.deleteFacility(id);
    }
}