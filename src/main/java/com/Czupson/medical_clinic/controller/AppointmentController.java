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

import java.time.LocalDateTime;

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
    public PageDto<AppointmentDto> getPatientAppointments(
            @PathVariable Long patientId,
            Pageable pageable) {
        log.info("GET /api/appointments/patient/{} - page={}, size={}", patientId, pageable.getPageNumber(), pageable.getPageSize());
        return appointmentService.getPatientAppointments(patientId, pageable);
    }

    @Operation(summary = "Get available appointments for doctor", description = "Returns a paginated list of available appointments for the specified doctor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available appointments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/doctor/{doctorId}/available")
    public PageDto<AppointmentDto> getAvailableAppointmentsForDoctor(
            @PathVariable Long doctorId,
            Pageable pageable) {
        log.info("GET /api/appointments/doctor/{}/available - page={}, size={}", doctorId, pageable.getPageNumber(), pageable.getPageSize());
        return appointmentService.getAvailableAppointmentsForDoctor(doctorId, pageable);
    }

    @GetMapping("/available")
    public PageDto<AppointmentDto> getAvailableAppointmentsBySpecialization(
            @RequestParam(required = false) String specialization,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            Pageable pageable) {
        log.info("GET /api/appointments/available - specialization={}, start={}, end={}, page={}, size={}", specialization, start, end, pageable.getPageNumber(), pageable.getPageSize());
        return appointmentService.getAvailableAppointmentsBySpecialization(specialization, start, end, pageable);
    }

    @DeleteMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancel an appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Appointment cancelled"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "409", description = "Appointment is not booked"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void cancelAppointment(@PathVariable Long id) {
        log.info("Cancelling appointment: id={}", id);
        appointmentService.cancelAppointment(id);
    }

    @GetMapping("/doctor/{doctorId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all appointments for a doctor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public PageDto<AppointmentDto> getDoctorAppointments(
            @PathVariable Long doctorId,
            Pageable pageable) {
        log.info("Getting appointments for doctor: doctorId={}", doctorId);
        return appointmentService.getDoctorAppointments(doctorId, pageable);
    }

    @GetMapping("/specialization/{specialization}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get appointments by specialization and time range")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully")
    })
    public PageDto<AppointmentDto> getAppointmentsBySpecializationAndTimeRange(
            @PathVariable String specialization,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            Pageable pageable) {
        log.info("Getting appointments by specialization and time range: " + "specialization={}, start={}, end={}", specialization, start, end);
        return appointmentService.getAppointmentsBySpecializationAndTimeRange(specialization, start, end, pageable);
    }
}