package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.patient.CreatePatientCommand;
import com.Czupson.medical_clinic.dto.patient.PatientDto;
import com.Czupson.medical_clinic.dto.patient.UpdatePatientCommand;
import com.Czupson.medical_clinic.service.PatientService;
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
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService service;

    @Operation(summary = "Get all patients", description = "Returns a list of all patients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients retrieved successfully")
    })
    @GetMapping
    public PageDto<PatientDto> getAllPatients(Pageable pageable) {
        log.info("GET /api/patients - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return service.getAllPatients(pageable);
    }

    @Operation(summary = "Get patient by id", description = "Returns patient with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/{id}")
    public PatientDto getPatient(@PathVariable Long id) {
        log.info("GET /api/patients/{} - retrieving patient", id);
        return service.getPatient(id);
    }

    @Operation(summary = "Create patient", description = "Creates a new patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid patient data"),
            @ApiResponse(responseCode = "409", description = "Patient already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@RequestBody CreatePatientCommand command) {
        log.info("POST /api/patients - creating patient: userId={}, firstName={}, lastName={}, " + "idCardNo={}, phoneNumber={}, birthday={}", command.userId(), command.firstName(), command.lastName(), command.idCardNo(), command.phoneNumber(), command.birthday());
        return service.addPatient(command);
    }

    @Operation(summary = "Update patient", description = "Updates an existing patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid patient data"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "409", description = "Patient already exists")
    })
    @PutMapping("/{id}")
    public PatientDto updatePatient(@PathVariable Long id,
                                    @RequestBody UpdatePatientCommand command) {
        log.info("PUT /api/patients/{} - updating patient: firstName={}, lastName={}, " + "idCardNo={}, phoneNumber={}, birthday={}", id, command.firstName(), command.lastName(), command.idCardNo(), command.phoneNumber(), command.birthday());
        return service.updatePatient(id, command);
    }

    @Operation(summary = "Delete patient", description = "Deletes patient with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable Long id) {
        log.info("DELETE /api/patients/{} - deleting patient", id);
        service.deletePatient(id);
    }
}
