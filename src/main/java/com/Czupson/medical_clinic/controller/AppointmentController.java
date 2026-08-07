package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.appointment.AppointmentDto;
import com.Czupson.medical_clinic.dto.appointment.BookAppointmentCommand;
import com.Czupson.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.Czupson.medical_clinic.model.Appointment;
import com.Czupson.medical_clinic.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Operation(summary = "Get all appointments", description = "Returns a paginated list of appointments")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully")
    })
    @GetMapping
    public PageDto<AppointmentDto> getAllAppointments(Pageable pageable) {
        return appointmentService.getAllAppointments(pageable);
    }

    @Operation(summary = "Get appointment by id", description = "Returns appointment with the specified id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment found"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @GetMapping("/{id}")
    public Appointment getAppointment(@PathVariable Long id) {
        return appointmentService.getAppointment(id);
    }

    @Operation(summary = "Create appointment", description = "Creates a new appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid appointment data"),
            @ApiResponse(responseCode = "409", description = "Appointment conflict")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment addAppointment(@RequestBody CreateAppointmentCommand command) {
        return appointmentService.addAppointment(command);
    }

    @Operation(summary = "Book appointment", description = "Books an existing appointment for a patient")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment booked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid booking request"),
            @ApiResponse(responseCode = "404", description = "Appointment or patient not found"),
            @ApiResponse(responseCode = "409", description = "Appointment cannot be booked")
    })
    @PatchMapping("/{id}/book")
    public Appointment bookAppointment(
            @PathVariable Long id,
            @RequestBody BookAppointmentCommand command) {
        return appointmentService.bookAppointment(id, command);
    }

    @Operation(summary = "Delete appointment", description = "Deletes appointment with the specified id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Appointment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
    }

    @Operation(summary = "Get patient appointments", description = "Returns all appointments for the specified patient")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/patient/{patientId}")
    public List<Appointment> getPatientAppointments(
            @PathVariable Long patientId) {
        return appointmentService.getPatientAppointments(patientId);
    }
}