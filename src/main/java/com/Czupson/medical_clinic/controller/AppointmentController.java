package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.appointment.AppointmentDto;
import com.Czupson.medical_clinic.dto.appointment.BookAppointmentCommand;
import com.Czupson.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.Czupson.medical_clinic.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
        log.info("GET /api/appointments - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return appointmentService.getAllAppointments(pageable);
    }

    @Operation(summary = "Get appointment by id", description = "Returns appointment with the specified id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment found"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @GetMapping("/{id}")
    public AppointmentDto getAppointment(@PathVariable Long id) {
        log.info("GET /api/appointments/{} - retrieving appointment", id);
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
    public AppointmentDto addAppointment(@RequestBody CreateAppointmentCommand command) {
        log.info("POST /api/appointments - creating appointment: doctorId={}, start={}, end={}", command.doctorId(), command.appointmentStart(), command.appointmentEnd());
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
    public AppointmentDto bookAppointment(
            @PathVariable Long id,
            @RequestBody BookAppointmentCommand command) {
        log.info("PATCH /api/appointments/{}/book - booking appointment: patientId={}", id, command.patientId());
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
        log.info("DELETE /api/appointments/{} - deleting appointment", id);
        appointmentService.deleteAppointment(id);
    }

    @Operation(summary = "Get patient appointments", description = "Returns all appointments for the specified patient")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/patient/{patientId}")
    public List<AppointmentDto> getPatientAppointments(
            @PathVariable Long patientId) {
        log.info("GET /api/appointments/patient/{} - retrieving patient appointments", patientId);
        return appointmentService.getPatientAppointments(patientId);
    }
}