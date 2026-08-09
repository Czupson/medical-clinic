package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.facility.CreateFacilityCommand;
import com.Czupson.medical_clinic.dto.facility.FacilityDto;
import com.Czupson.medical_clinic.dto.facility.UpdateFacilityCommand;
import com.Czupson.medical_clinic.service.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @Operation(summary = "Get all facilities", description = "Returns a paginated list of all facilities")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Facilities retrieved successfully")
    })
    @GetMapping
    public PageDto<FacilityDto> getAllFacilities(Pageable pageable) {
        return facilityService.getAllFacilities(pageable);
    }

    @Operation(summary = "Get facility by id", description = "Returns facility with the specified id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Facility found"),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    @GetMapping("/{id}")
    public FacilityDto getFacility(@PathVariable Long id) {
        return facilityService.getFacility(id);
    }

    @Operation(summary = "Create facility", description = "Creates a new facility")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Facility created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid facility data"),
            @ApiResponse(responseCode = "409", description = "Facility already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityDto addFacility(@RequestBody CreateFacilityCommand command) {
        return facilityService.addFacility(command);
    }

    @Operation(summary = "Update facility", description = "Updates an existing facility")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Facility updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid facility data"),
            @ApiResponse(responseCode = "404", description = "Facility not found"),
            @ApiResponse(responseCode = "409", description = "Facility already exists")
    })
    @PutMapping("/{id}")
    public FacilityDto updateFacility(@PathVariable Long id,
                                   @RequestBody UpdateFacilityCommand command) {
        return facilityService.updateFacility(id, command);
    }

    @Operation(summary = "Delete facility", description = "Deletes facility with the specified id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Facility deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFacility(@PathVariable Long id) {
        facilityService.deleteFacility(id);
    }
}