package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.model.Patient;
import com.Czupson.medical_clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
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
    public Patient addPatient(@RequestBody Patient patient) {
        return service.addPatient(patient);
    }

    @PutMapping("/{email}")
    public Patient updatePatient(@PathVariable String email, @RequestBody Patient patient) {
        return service.updatePatient(email, patient);
    }

    @DeleteMapping("/{email}")
    public void deletePatient(@PathVariable String email) {
        service.deletePatient(email);
    }

    @PatchMapping("/{email}/password")
    public void changePassword(@PathVariable String email,
                               @RequestBody String newPassword){
        service.changePassword(email, newPassword);
    }
}
