package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.appointment.AppointmentDto;
import com.Czupson.medical_clinic.dto.appointment.BookAppointmentCommand;
import com.Czupson.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.Czupson.medical_clinic.exception.appointment.*;
import com.Czupson.medical_clinic.exception.doctor.DoctorNotFoundException;
import com.Czupson.medical_clinic.exception.patient.PatientNotFoundException;
import com.Czupson.medical_clinic.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    void getAppointment_AppointmentExists_AppointmentReturned() throws Exception {
        // given
        Long appointmentId = 1L;
        AppointmentDto appointmentDto = new AppointmentDto(
                appointmentId, LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0), 1L, 2L);
        when(appointmentService.getAppointment(appointmentId)).thenReturn(appointmentDto);
        // when & then
        mockMvc.perform(get("/api/appointments/{id}", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.patientId").value(2))
                .andExpect(jsonPath("$.appointmentStart")
                        .value("2026-09-01T10:00:00"))
                .andExpect(jsonPath("$.appointmentEnd")
                        .value("2026-09-01T11:00:00"));
        verify(appointmentService).getAppointment(appointmentId);
    }

    @Test
    void addAppointment_ValidCommand_AppointmentCreated() throws Exception {
        // given
        CreateAppointmentCommand command = new CreateAppointmentCommand(1L,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0));
        AppointmentDto appointmentDto = new AppointmentDto(1L, command.appointmentStart(), command.appointmentEnd(), 1L, null);
        when(appointmentService.addAppointment(any(CreateAppointmentCommand.class))).thenReturn(appointmentDto);
        // when & then
        mockMvc.perform(post("/api/appointments")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.appointmentStart")
                        .value("2026-09-01T10:00:00"))
                .andExpect(jsonPath("$.appointmentEnd")
                        .value("2026-09-01T11:00:00"));
        ArgumentCaptor<CreateAppointmentCommand> captor = ArgumentCaptor.forClass(CreateAppointmentCommand.class);
        verify(appointmentService).addAppointment(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void bookAppointment_ValidCommand_AppointmentBooked() throws Exception {
        // given
        Long appointmentId = 1L;
        BookAppointmentCommand command = new BookAppointmentCommand(2L);
        AppointmentDto appointmentDto = new AppointmentDto(appointmentId,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0), 1L, 2L);
        when(appointmentService.bookAppointment(eq(appointmentId), any(BookAppointmentCommand.class))).thenReturn(appointmentDto);
        // when & then
        mockMvc.perform(patch(
                        "/api/appointments/{id}/book",
                        appointmentId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.patientId").value(2));
        ArgumentCaptor<BookAppointmentCommand> captor = ArgumentCaptor.forClass(BookAppointmentCommand.class);
        verify(appointmentService).bookAppointment(eq(appointmentId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void deleteAppointment_AppointmentExists_AppointmentDeleted() throws Exception {
        // given
        Long appointmentId = 1L;
        // when & then
        mockMvc.perform(delete("/api/appointments/{id}", appointmentId))
                .andExpect(status().isNoContent());
        verify(appointmentService).deleteAppointment(appointmentId);
    }

    @Test
    void getPatientAppointments_AppointmentsExist_AppointmentsReturned()
            throws Exception {
        // given
        Long patientId = 2L;
        AppointmentDto appointmentDto = new AppointmentDto(1L,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0), 1L, patientId);
        when(appointmentService.getPatientAppointments(patientId)).thenReturn(List.of(appointmentDto));
        // when & then
        mockMvc.perform(get(
                        "/api/appointments/patient/{patientId}",
                        patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(1))
                .andExpect(jsonPath("$[0].patientId").value(2))
                .andExpect(jsonPath("$[0].appointmentStart")
                        .value("2026-09-01T10:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd")
                        .value("2026-09-01T11:00:00"));
        verify(appointmentService).getPatientAppointments(patientId);
    }

    @Test
    void getAllAppointments_AppointmentsExist_AppointmentsReturned() throws Exception {
        // given
        AppointmentDto appointmentDto = new AppointmentDto(1L,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0), 1L, 2L);
        PageDto<AppointmentDto> pageDto = new PageDto<>(List.of(appointmentDto), 0, 10, 1L, 1);
        when(appointmentService.getAllAppointments(any(Pageable.class))).thenReturn(pageDto);
        // when & then
        mockMvc.perform(get("/api/appointments")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].appointmentStart")
                        .value("2026-09-01T10:00:00"))
                .andExpect(jsonPath("$.content[0].appointmentEnd")
                        .value("2026-09-01T11:00:00"))
                .andExpect(jsonPath("$.content[0].doctorId").value(1))
                .andExpect(jsonPath("$.content[0].patientId").value(2))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
        verify(appointmentService).getAllAppointments(any(Pageable.class));
    }

    @Test
    void getAppointment_AppointmentDoesNotExist_NotFound() throws Exception {
        // given
        Long appointmentId = 1L;
        when(appointmentService.getAppointment(appointmentId)).thenThrow(new AppointmentNotFoundException(appointmentId));
        // when & then
        mockMvc.perform(get("/api/appointments/{id}", appointmentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        verify(appointmentService).getAppointment(appointmentId);
    }

    @Test
    void addAppointment_AppointmentConflict_Conflict() throws Exception {
        // given
        CreateAppointmentCommand command = new CreateAppointmentCommand(1L,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0));
        when(appointmentService.addAppointment(any(CreateAppointmentCommand.class))).
                thenThrow(new AppointmentAlreadyExistsException());
        // when & then
        mockMvc.perform(post("/api/appointments")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        ArgumentCaptor<CreateAppointmentCommand> captor = ArgumentCaptor.forClass(CreateAppointmentCommand.class);
        verify(appointmentService).addAppointment(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void bookAppointment_AppointmentDoesNotExist_NotFound() throws Exception {
        // given
        Long appointmentId = 1L;
        BookAppointmentCommand command = new BookAppointmentCommand(2L);
        when(appointmentService.bookAppointment(eq(appointmentId), any(BookAppointmentCommand.class))).
                thenThrow(new AppointmentNotFoundException(appointmentId));
        // when & then
        mockMvc.perform(patch(
                        "/api/appointments/{id}/book",
                        appointmentId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        ArgumentCaptor<BookAppointmentCommand> captor = ArgumentCaptor.forClass(BookAppointmentCommand.class);
        verify(appointmentService).bookAppointment(eq(appointmentId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void bookAppointment_PatientDoesNotExist_NotFound() throws Exception {
        // given
        Long appointmentId = 1L;
        BookAppointmentCommand command = new BookAppointmentCommand(2L);
        when(appointmentService.bookAppointment(eq(appointmentId), any(BookAppointmentCommand.class))).
                thenThrow(new PatientNotFoundException(2L));
        // when & then
        mockMvc.perform(patch(
                        "/api/appointments/{id}/book",
                        appointmentId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        ArgumentCaptor<BookAppointmentCommand> captor = ArgumentCaptor.forClass(BookAppointmentCommand.class);
        verify(appointmentService).bookAppointment(eq(appointmentId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void bookAppointment_AppointmentInPast_BadRequest() throws Exception {
        // given
        Long appointmentId = 1L;
        BookAppointmentCommand command = new BookAppointmentCommand(2L);
        when(appointmentService.bookAppointment(eq(appointmentId), any(BookAppointmentCommand.class)))
                .thenThrow(new AppointmentDataValidationException("Cannot book an appointment in the past"));
        // when & then
        mockMvc.perform(patch(
                        "/api/appointments/{id}/book",
                        appointmentId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Cannot book an appointment in the past"));
        ArgumentCaptor<BookAppointmentCommand> captor = ArgumentCaptor.forClass(BookAppointmentCommand.class);
        verify(appointmentService).bookAppointment(eq(appointmentId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void bookAppointment_AppointmentAlreadyBooked_Conflict() throws Exception {
        // given
        Long appointmentId = 1L;
        BookAppointmentCommand command = new BookAppointmentCommand(2L);
        when(appointmentService.bookAppointment(eq(appointmentId), any(BookAppointmentCommand.class))).
                thenThrow(new AppointmentAlreadyBookedException());
        // when & then
        mockMvc.perform(patch(
                        "/api/appointments/{id}/book",
                        appointmentId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        ArgumentCaptor<BookAppointmentCommand> captor = ArgumentCaptor.forClass(BookAppointmentCommand.class);
        verify(appointmentService).bookAppointment(eq(appointmentId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void bookAppointment_PatientAppointmentConflict_Conflict() throws Exception {
        // given
        Long appointmentId = 1L;
        BookAppointmentCommand command = new BookAppointmentCommand(2L);
        when(appointmentService.bookAppointment(eq(appointmentId), any(BookAppointmentCommand.class)))
                .thenThrow(new PatientAppointmentConflictException());
        // when & then
        mockMvc.perform(patch(
                        "/api/appointments/{id}/book",
                        appointmentId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        ArgumentCaptor<BookAppointmentCommand> captor = ArgumentCaptor.forClass(BookAppointmentCommand.class);
        verify(appointmentService).bookAppointment(eq(appointmentId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void deleteAppointment_AppointmentDoesNotExist_NotFound() throws Exception {
        // given
        Long appointmentId = 1L;
        doThrow(new AppointmentNotFoundException(appointmentId)).when(appointmentService).deleteAppointment(appointmentId);
        // when & then
        mockMvc.perform(delete("/api/appointments/{id}", appointmentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        verify(appointmentService).deleteAppointment(appointmentId);
    }

    @Test
    void getPatientAppointments_PatientDoesNotExist_NotFound() throws Exception {
        // given
        Long patientId = 2L;
        when(appointmentService.getPatientAppointments(patientId)).thenThrow(new PatientNotFoundException(patientId));
        // when & then
        mockMvc.perform(get(
                        "/api/appointments/patient/{patientId}",
                        patientId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        verify(appointmentService).getPatientAppointments(patientId);
    }

    @Test
    void addAppointment_DoctorDoesNotExist_NotFound() throws Exception {
        // given
        CreateAppointmentCommand command = new CreateAppointmentCommand(1L,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0));
        when(appointmentService.addAppointment(any(CreateAppointmentCommand.class))).thenThrow(new DoctorNotFoundException(1L));
        // when & then
        mockMvc.perform(post("/api/appointments")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        ArgumentCaptor<CreateAppointmentCommand> captor = ArgumentCaptor.forClass(CreateAppointmentCommand.class);
        verify(appointmentService).addAppointment(captor.capture());
        assertEquals(command, captor.getValue());
    }
}
