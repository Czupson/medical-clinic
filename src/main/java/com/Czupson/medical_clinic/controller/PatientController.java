package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.ChangePasswordCommand;
import com.Czupson.medical_clinic.dto.CreatePatientCommand;
import com.Czupson.medical_clinic.dto.UpdatePatientCommand;
import com.Czupson.medical_clinic.model.Patient;
import com.Czupson.medical_clinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<Patient> getAllPatients() {
        return service.getAllPatients();
    }

    @Operation(summary = "Get patient by email", description = "Returns patient with the specified email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/{email}")
    public Patient getPatient(@PathVariable String email) {
        return service.getPatient(email);
    }

    @Operation(summary = "Create patient", description = "Creates a new patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid patient data"),
            @ApiResponse(responseCode = "409", description = "Patient already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient addPatient(@RequestBody CreatePatientCommand command) {
        return service.addPatient(command);
    }

    @Operation(summary = "Update patient", description = "Updates an existing patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid patient data"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "409", description = "Patient already exists")
    })
    @PutMapping("/{email}")
    public Patient updatePatient(@PathVariable String email, @RequestBody UpdatePatientCommand command) {
        return service.updatePatient(email, command);
    }

    @Operation(summary = "Delete patient", description = "Deletes patient with the specified email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable String email) {
        service.deletePatient(email);
    }

    @Operation(summary = "Change patient password", description = "Changes patient's password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PatchMapping("/{email}/password")
    public void changePassword(@PathVariable String email,
                               @RequestBody ChangePasswordCommand command) {
        service.changePassword(email, command);
    }
}
