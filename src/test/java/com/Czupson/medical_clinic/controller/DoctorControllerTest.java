package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.Czupson.medical_clinic.dto.doctor.DoctorDto;
import com.Czupson.medical_clinic.dto.doctor.UpdateDoctorCommand;
import com.Czupson.medical_clinic.exception.doctor.DoctorAlreadyExistsException;
import com.Czupson.medical_clinic.exception.doctor.DoctorDataValidationException;
import com.Czupson.medical_clinic.exception.doctor.DoctorNotFoundException;
import com.Czupson.medical_clinic.exception.facility.FacilitiesNotFoundException;
import com.Czupson.medical_clinic.exception.user.UserNotFoundException;
import com.Czupson.medical_clinic.service.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DoctorService doctorService;

    @Test
    void getDoctor_DoctorExists_DoctorReturned() throws Exception {
        // given
        Long doctorId = 1L;
        DoctorDto doctorDto = new DoctorDto(doctorId, "Jan", "Kowalski", "Kardiolog");
        when(doctorService.getDoctor(doctorId)).thenReturn(doctorDto);
        // when & then
        mockMvc.perform(get("/api/doctors/{id}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.specialization").value("Kardiolog"));
        verify(doctorService).getDoctor(doctorId);
    }

    @Test
    void getAllDoctors_DoctorsExist_DoctorsReturned() throws Exception {
        // given
        DoctorDto doctorDto = new DoctorDto(1L, "Jan", "Kowalski", "Kardiolog");
        PageDto<DoctorDto> pageDto = new PageDto<>(List.of(doctorDto), 0, 10, 1L, 1);
        when(doctorService.getAllDoctors(any(Pageable.class))).thenReturn(pageDto);
        // when & then
        mockMvc.perform(get("/api/doctors")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("Jan"))
                .andExpect(jsonPath("$.content[0].lastName").value("Kowalski"))
                .andExpect(jsonPath("$.content[0].specialization").value("Kardiolog"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
        verify(doctorService).getAllDoctors(any(Pageable.class));
    }

    @Test
    void addDoctor_ValidCommand_DoctorCreated() throws Exception {
        // given
        CreateDoctorCommand command = new CreateDoctorCommand(1L, Set.of(1L, 2L), "Jan", "Kowalski", "Kardiolog");
        DoctorDto doctorDto = new DoctorDto(1L, "Jan", "Kowalski", "Kardiolog");
        when(doctorService.addDoctor(any(CreateDoctorCommand.class))).thenReturn(doctorDto);
        // when & then
        mockMvc.perform(post("/api/doctors")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.specialization").value("Kardiolog"));
        ArgumentCaptor<CreateDoctorCommand> captor = ArgumentCaptor.forClass(CreateDoctorCommand.class);
        verify(doctorService).addDoctor(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void updateDoctor_ValidCommand_DoctorUpdated() throws Exception {
        // given
        Long doctorId = 1L;
        UpdateDoctorCommand command = new UpdateDoctorCommand(Set.of(1L, 2L), "Adam", "Nowak", "Neurolog");
        DoctorDto doctorDto = new DoctorDto(doctorId, "Adam", "Nowak", "Neurolog");
        when(doctorService.updateDoctor(eq(doctorId), any(UpdateDoctorCommand.class))).thenReturn(doctorDto);
        // when & then
        mockMvc.perform(put("/api/doctors/{id}", doctorId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Adam"))
                .andExpect(jsonPath("$.lastName").value("Nowak"))
                .andExpect(jsonPath("$.specialization").value("Neurolog"));
        ArgumentCaptor<UpdateDoctorCommand> captor = ArgumentCaptor.forClass(UpdateDoctorCommand.class);
        verify(doctorService).updateDoctor(eq(doctorId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void deleteDoctor_DoctorExists_DoctorDeleted() throws Exception {
        // given
        Long doctorId = 1L;
        // when & then
        mockMvc.perform(delete("/api/doctors/{id}", doctorId))
                .andExpect(status().isNoContent());
        verify(doctorService).deleteDoctor(doctorId);
    }

    @Test
    void getDoctor_DoctorDoesNotExist_NotFound() throws Exception {
        // given
        Long doctorId = 1L;
        when(doctorService.getDoctor(doctorId)).thenThrow(new DoctorNotFoundException(doctorId));
        // when & then
        mockMvc.perform(get("/api/doctors/{id}", doctorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        verify(doctorService).getDoctor(doctorId);
    }

    @Test
    void addDoctor_UserDoesNotExist_NotFound() throws Exception {
        // given
        CreateDoctorCommand command = new CreateDoctorCommand(1L, Set.of(1L, 2L), "Jan", "Kowalski", "Kardiolog");
        when(doctorService.addDoctor(any(CreateDoctorCommand.class))).thenThrow(new UserNotFoundException(1L));
        // when & then
        mockMvc.perform(post("/api/doctors")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        ArgumentCaptor<CreateDoctorCommand> captor = ArgumentCaptor.forClass(CreateDoctorCommand.class);
        verify(doctorService).addDoctor(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void addDoctor_FacilitiesDoNotExist_NotFound() throws Exception {
        // given
        CreateDoctorCommand command = new CreateDoctorCommand(1L, Set.of(1L, 2L), "Jan", "Kowalski", "Kardiolog");
        when(doctorService.addDoctor(any(CreateDoctorCommand.class))).thenThrow(new FacilitiesNotFoundException());
        // when & then
        mockMvc.perform(post("/api/doctors")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        ArgumentCaptor<CreateDoctorCommand> captor = ArgumentCaptor.forClass(CreateDoctorCommand.class);
        verify(doctorService).addDoctor(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void addDoctor_DoctorAlreadyExists_Conflict() throws Exception {
        // given
        CreateDoctorCommand command = new CreateDoctorCommand(1L, Set.of(1L, 2L), "Jan", "Kowalski", "Kardiolog");
        when(doctorService.addDoctor(any(CreateDoctorCommand.class))).thenThrow(new DoctorAlreadyExistsException(1L));
        // when & then
        mockMvc.perform(post("/api/doctors")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        ArgumentCaptor<CreateDoctorCommand> captor = ArgumentCaptor.forClass(CreateDoctorCommand.class);
        verify(doctorService).addDoctor(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void updateDoctor_DoctorDoesNotExist_NotFound() throws Exception {
        // given
        Long doctorId = 1L;
        UpdateDoctorCommand command = new UpdateDoctorCommand(Set.of(1L, 2L), "Adam", "Nowak", "Neurolog");
        when(doctorService.updateDoctor(eq(doctorId), any(UpdateDoctorCommand.class))).thenThrow(new DoctorNotFoundException(doctorId));
        // when & then
        mockMvc.perform(put("/api/doctors/{id}", doctorId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        ArgumentCaptor<UpdateDoctorCommand> captor = ArgumentCaptor.forClass(UpdateDoctorCommand.class);
        verify(doctorService).updateDoctor(eq(doctorId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void updateDoctor_FacilitiesDoNotExist_NotFound() throws Exception {
        // given
        Long doctorId = 1L;
        UpdateDoctorCommand command = new UpdateDoctorCommand(Set.of(1L, 2L), "Adam", "Nowak", "Neurolog");
        when(doctorService.updateDoctor(eq(doctorId), any(UpdateDoctorCommand.class))).thenThrow(new FacilitiesNotFoundException());
        // when & then
        mockMvc.perform(put("/api/doctors/{id}", doctorId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        ArgumentCaptor<UpdateDoctorCommand> captor = ArgumentCaptor.forClass(UpdateDoctorCommand.class);
        verify(doctorService).updateDoctor(eq(doctorId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void updateDoctor_DoctorAlreadyExists_Conflict() throws Exception {
        // given
        Long doctorId = 1L;
        UpdateDoctorCommand command = new UpdateDoctorCommand(Set.of(1L, 2L), "Adam", "Nowak", "Neurolog");
        when(doctorService.updateDoctor(eq(doctorId), any(UpdateDoctorCommand.class))).thenThrow(new DoctorAlreadyExistsException(1L));
        // when & then
        mockMvc.perform(put("/api/doctors/{id}", doctorId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        ArgumentCaptor<UpdateDoctorCommand> captor = ArgumentCaptor.forClass(UpdateDoctorCommand.class);
        verify(doctorService).updateDoctor(eq(doctorId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void deleteDoctor_DoctorDoesNotExist_NotFound() throws Exception {
        // given
        Long doctorId = 1L;
        doThrow(new DoctorNotFoundException(doctorId)).when(doctorService).deleteDoctor(doctorId);
        // when & then
        mockMvc.perform(delete("/api/doctors/{id}", doctorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        verify(doctorService).deleteDoctor(doctorId);
    }

    @Test
    void addDoctor_InvalidData_BadRequest() throws Exception {
        // given
        CreateDoctorCommand command = new CreateDoctorCommand(1L, Set.of(1L, 2L), "", "Kowalski", "Kardiolog");
        when(doctorService.addDoctor(any(CreateDoctorCommand.class))).thenThrow(new DoctorDataValidationException("First name cannot be empty"));
        // when & then
        mockMvc.perform(post("/api/doctors")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("First name cannot be empty"));
        ArgumentCaptor<CreateDoctorCommand> captor = ArgumentCaptor.forClass(CreateDoctorCommand.class);
        verify(doctorService).addDoctor(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void updateDoctor_InvalidData_BadRequest() throws Exception {
        // given
        Long doctorId = 1L;
        UpdateDoctorCommand command = new UpdateDoctorCommand(Set.of(1L, 2L), "", "Nowak", "Neurolog");
        when(doctorService.updateDoctor(eq(doctorId), any(UpdateDoctorCommand.class))).thenThrow(new DoctorDataValidationException("First name cannot be empty"));
        // when & then
        mockMvc.perform(put("/api/doctors/{id}", doctorId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("First name cannot be empty"));
        ArgumentCaptor<UpdateDoctorCommand> captor = ArgumentCaptor.forClass(UpdateDoctorCommand.class);
        verify(doctorService).updateDoctor(eq(doctorId), captor.capture());
        assertEquals(command, captor.getValue());
    }
}
