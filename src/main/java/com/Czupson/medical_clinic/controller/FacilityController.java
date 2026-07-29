package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.facility.CreateFacilityCommand;
import com.Czupson.medical_clinic.dto.facility.UpdateFacilityCommand;
import com.Czupson.medical_clinic.model.Facility;
import com.Czupson.medical_clinic.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    public List<Facility> getAllFacilities() {
        return facilityService.getAllFacilities();
    }

    @GetMapping("/{name}")
    public Facility getFacility(@PathVariable String name) {
        return facilityService.getFacility(name);
    }

    @PostMapping
    public Facility addFacility(@RequestBody CreateFacilityCommand command) {
        return facilityService.addFacility(command);
    }

    @PutMapping("/{name}")
    public Facility updateFacility(@PathVariable String name,
                                   @RequestBody UpdateFacilityCommand command) {
        return facilityService.updateFacility(name, command);
    }

    @DeleteMapping("/{name}")
    public void deleteFacility(@PathVariable String name) {
        facilityService.deleteFacility(name);
    }
}