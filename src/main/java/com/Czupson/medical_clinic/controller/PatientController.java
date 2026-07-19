package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.ChangePasswordCommand;
import com.Czupson.medical_clinic.dto.CreatePatientCommand;
import com.Czupson.medical_clinic.dto.UpdatePatientCommand;
import com.Czupson.medical_clinic.model.Patient;
import com.Czupson.medical_clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService service;

    @GetMapping
    public List<Patient> getAllPatients() {
        return service.getAllPatients();
    }

    @GetMapping("/{email}")
    public Patient getPatient(@PathVariable String email) {
        return service.getPatient(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient addPatient(@RequestBody CreatePatientCommand command) {
        return service.addPatient(command);
    }

    @PutMapping("/{email}")
    public Patient updatePatient(@PathVariable String email, @RequestBody UpdatePatientCommand command) {
        return service.updatePatient(email, command);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable String email) {
        service.deletePatient(email);
    }

    @PatchMapping("/{email}/password")
    public void changePassword(@PathVariable String email,
                               @RequestBody ChangePasswordCommand command) {
        service.changePassword(email, command);
    }
}
