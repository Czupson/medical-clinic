package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.appointment.BookAppointmentCommand;
import com.Czupson.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.Czupson.medical_clinic.model.Appointment;
import com.Czupson.medical_clinic.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public Appointment getAppointment(@PathVariable Long id) {
        return appointmentService.getAppointment(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment addAppointment(@RequestBody CreateAppointmentCommand command) {
        return appointmentService.addAppointment(command);
    }

    @PatchMapping("/{id}/book")
    public Appointment bookAppointment(
            @PathVariable Long id,
            @RequestBody BookAppointmentCommand command) {
        return appointmentService.bookAppointment(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> getPatientAppointments(
            @PathVariable Long patientId) {
        return appointmentService.getPatientAppointments(patientId);
    }
}