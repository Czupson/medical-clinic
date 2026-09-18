package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.patient.CreatePatientCommand;
import com.Czupson.medical_clinic.dto.patient.PatientDto;
import com.Czupson.medical_clinic.dto.patient.UpdatePatientCommand;
import com.Czupson.medical_clinic.exception.patient.PatientAlreadyExistsException;
import com.Czupson.medical_clinic.exception.patient.PatientDataValidationException;
import com.Czupson.medical_clinic.exception.patient.PatientNotFoundException;
import com.Czupson.medical_clinic.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getPatient_PatientExists_PatientReturned() throws Exception {
        // given
        Long patientId = 1L;
        PatientDto patientDto = new PatientDto(patientId, "ABC123456", "Jan", "Kowalski", "123456789", null);
        when(patientService.getPatient(patientId)).thenReturn(patientDto);
        // when and then
        mockMvc.perform(get("/api/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idCardNo").value("ABC123456"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"));
        verify(patientService).getPatient(patientId);
    }

    @Test
    void getAllPatients_PatientsExist_PatientsReturned() throws Exception {
        // given
        PatientDto patientDto = new PatientDto(1L, "ABC123456", "Jan", "Kowalski", "123456789", null);
        PageDto<PatientDto> pageDto = new PageDto<>(List.of(patientDto), 0, 10, 1, 1);
        when(patientService.getAllPatients(any(Pageable.class))).thenReturn(pageDto);
        // when and then
        mockMvc.perform(get("/api/patients")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].idCardNo").value("ABC123456"))
                .andExpect(jsonPath("$.content[0].firstName").value("Jan"))
                .andExpect(jsonPath("$.content[0].lastName").value("Kowalski"))
                .andExpect(jsonPath("$.content[0].phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
        verify(patientService).getAllPatients(any(Pageable.class));
    }

    @Test
    void addPatient_ValidCommand_PatientCreated() throws Exception {
        // given
        CreatePatientCommand command = new CreatePatientCommand(1L, "ABC123456", "Jan", "Kowalski",
                "123456789", LocalDate.of(1990, 1, 1));
        PatientDto patientDto = new PatientDto(1L, "ABC123456", "Jan", "Kowalski",
                "123456789", null);
        when(patientService.addPatient(any(CreatePatientCommand.class))).thenReturn(patientDto);
        // when & then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idCardNo").value("ABC123456"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"));
        ArgumentCaptor<CreatePatientCommand> captor = ArgumentCaptor.forClass(CreatePatientCommand.class);
        verify(patientService).addPatient(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void updatePatient_ValidCommand_PatientUpdated() throws Exception {
        // given
        Long patientId = 1L;
        UpdatePatientCommand command = new UpdatePatientCommand(patientId, "ABC654321", "Adam", "Nowak",
                "987654321", LocalDate.of(1991, 2, 2));
        PatientDto patientDto = new PatientDto(patientId, "ABC654321", "Adam", "Nowak",
                "987654321", LocalDate.of(1991, 2, 2));
        when(patientService.updatePatient(eq(patientId), any(UpdatePatientCommand.class))).thenReturn(patientDto);
        // when & then
        mockMvc.perform(put("/api/patients/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idCardNo").value("ABC654321"))
                .andExpect(jsonPath("$.firstName").value("Adam"))
                .andExpect(jsonPath("$.lastName").value("Nowak"))
                .andExpect(jsonPath("$.phoneNumber").value("987654321"));
        ArgumentCaptor<UpdatePatientCommand> captor = ArgumentCaptor.forClass(UpdatePatientCommand.class);
        verify(patientService).updatePatient(eq(patientId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void deletePatient_PatientExists_PatientDeleted() throws Exception {
        // given
        Long patientId = 1L;
        doNothing().when(patientService).deletePatient(patientId);
        // when and then
        mockMvc.perform(delete("/api/patients/{id}", patientId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(patientService).deletePatient(patientId);
    }

    @Test
    void getPatient_PatientDoesNotExist_NotFound() throws Exception {
        // given
        Long patientId = 1L;
        when(patientService.getPatient(patientId)).thenThrow(new PatientNotFoundException(patientId));
        // when and then
        mockMvc.perform(get("/api/patients/{id}", patientId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        verify(patientService).getPatient(patientId);
    }

    @Test
    void addPatient_InvalidData_BadRequest() throws Exception {
        // given
        CreatePatientCommand command = new CreatePatientCommand(1L, "ABC123456", "", "Kowalski",
                "123456789", LocalDate.of(1990, 1, 1));
        when(patientService.addPatient(any(CreatePatientCommand.class))).thenThrow(new PatientDataValidationException("First name cannot be empty"));
        // when & then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("First name cannot be empty"));
        ArgumentCaptor<CreatePatientCommand> captor = ArgumentCaptor.forClass(CreatePatientCommand.class);
        verify(patientService).addPatient(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void addPatient_PatientAlreadyExists_Conflict() throws Exception {
        // given
        CreatePatientCommand command = new CreatePatientCommand(1L, "ABC123456", "Jan", "Kowalski",
                "123456789", LocalDate.of(1990, 1, 1));
        when(patientService.addPatient(any(CreatePatientCommand.class))).thenThrow(new PatientAlreadyExistsException("jan.kowalski@example.com"));
        // when & then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Patient already exists with email: jan.kowalski@example.com"));
        ArgumentCaptor<CreatePatientCommand> captor = ArgumentCaptor.forClass(CreatePatientCommand.class);
        verify(patientService).addPatient(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void updatePatient_PatientDoesNotExist_NotFound() throws Exception {
        // given
        Long patientId = 1L;
        UpdatePatientCommand command = new UpdatePatientCommand(patientId, "ABC654321", "Adam", "Nowak",
                "987654321", LocalDate.of(1991, 2, 2));
        when(patientService.updatePatient(eq(patientId), any(UpdatePatientCommand.class))).thenThrow(new PatientNotFoundException(patientId));
        // when & then
        mockMvc.perform(put("/api/patients/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        ArgumentCaptor<UpdatePatientCommand> captor = ArgumentCaptor.forClass(UpdatePatientCommand.class);
        verify(patientService).updatePatient(eq(patientId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void updatePatient_InvalidData_BadRequest() throws Exception {
        // given
        Long patientId = 1L;
        UpdatePatientCommand command = new UpdatePatientCommand(patientId, "ABC654321", "", "Nowak",
                "987654321", LocalDate.of(1991, 2, 2));
        when(patientService.updatePatient(eq(patientId), any(UpdatePatientCommand.class))).thenThrow(new PatientDataValidationException("First name cannot be empty"));
        // when & then
        mockMvc.perform(put("/api/patients/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("First name cannot be empty"));
        ArgumentCaptor<UpdatePatientCommand> captor = ArgumentCaptor.forClass(UpdatePatientCommand.class);
        verify(patientService).updatePatient(eq(patientId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void updatePatient_PatientAlreadyExists_Conflict() throws Exception {
        // given
        Long patientId = 1L;
        UpdatePatientCommand command = new UpdatePatientCommand(patientId, "ABC654321", "Adam", "Nowak",
                "987654321", LocalDate.of(1991, 2, 2));
        when(patientService.updatePatient(eq(patientId), any(UpdatePatientCommand.class))).thenThrow(new PatientAlreadyExistsException("jan.kowalski@example.com"));
        // when & then
        mockMvc.perform(put("/api/patients/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Patient already exists with email: jan.kowalski@example.com"));
        ArgumentCaptor<UpdatePatientCommand> captor = ArgumentCaptor.forClass(UpdatePatientCommand.class);
        verify(patientService).updatePatient(eq(patientId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void deletePatient_PatientDoesNotExist_NotFound() throws Exception {
        // given
        Long patientId = 1L;
        doThrow(new PatientNotFoundException(patientId)).when(patientService).deletePatient(patientId);
        // when and then
        mockMvc.perform(delete("/api/patients/{id}", patientId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        verify(patientService).deletePatient(patientId);
    }
}
