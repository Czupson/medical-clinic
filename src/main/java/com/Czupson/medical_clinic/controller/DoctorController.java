package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.Czupson.medical_clinic.dto.doctor.DoctorDto;
import com.Czupson.medical_clinic.dto.doctor.UpdateDoctorCommand;
import com.Czupson.medical_clinic.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(summary = "Get all doctors", description = "Returns a paginated list of all doctors")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully")
    })
    @GetMapping
    public PageDto<DoctorDto> getAllDoctors(Pageable pageable) {
        log.info("GET /api/doctors - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return doctorService.getAllDoctors(pageable);
    }

    @Operation(summary = "Get doctor by id", description = "Returns doctor with the specified id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/{id}")
    public DoctorDto getDoctor(@PathVariable Long id) {
        log.info("GET /api/doctors/{} - retrieving doctor", id);
        return doctorService.getDoctor(id);
    }

    @Operation(summary = "Create doctor", description = "Creates a new doctor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Doctor created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid doctor data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Doctor already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto addDoctor(@RequestBody CreateDoctorCommand command) {
        log.info("POST /api/doctors - creating doctor: userId={}, firstName={}, lastName={}, " + "specialization={}, facilityIds={}", command.userId(), command.firstName(), command.lastName(), command.specialization(), command.facilityIds());
        return doctorService.addDoctor(command);
    }

    @Operation(summary = "Update doctor", description = "Updates an existing doctor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid doctor data"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "409", description = "Doctor already exists")
    })
    @PutMapping("/{id}")
    public DoctorDto updateDoctor(@PathVariable Long id,
                               @RequestBody UpdateDoctorCommand command) {
        log.info("PUT /api/doctors/{} - updating doctor: firstName={}, lastName={}, " + "specialization={}, facilityIds={}", id, command.firstName(), command.lastName(), command.specialization(), command.facilityIds());
        return doctorService.updateDoctor(id, command);
    }

    @Operation(summary = "Delete doctor", description = "Deletes doctor with the specified id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDoctor(@PathVariable Long id) {
        log.info("DELETE /api/doctors/{} - deleting doctor", id);
        doctorService.deleteDoctor(id);
    }
}