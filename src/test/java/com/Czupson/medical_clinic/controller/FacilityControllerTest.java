package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.facility.CreateFacilityCommand;
import com.Czupson.medical_clinic.dto.facility.FacilityDto;
import com.Czupson.medical_clinic.dto.facility.UpdateFacilityCommand;
import com.Czupson.medical_clinic.exception.facility.FacilityAlreadyExistsException;
import com.Czupson.medical_clinic.exception.facility.FacilityNotFoundException;
import com.Czupson.medical_clinic.service.FacilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.springframework.http.MediaType;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(FacilityController.class)
public class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacilityService facilityService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getFacility_FacilityExists_FacilityReturned() throws Exception {
        //given
        Long facilityId = 1L;
        FacilityDto facilityDto = new FacilityDto(facilityId, "Przychodnia Młynowa", "Białystok", "15-001",
                "Młynowa", "17");
        when(facilityService.getFacility(facilityId)).thenReturn(facilityDto);
        //when and then
        mockMvc.perform(get("/api/facilities/{id}", facilityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Przychodnia Młynowa"))
                .andExpect(jsonPath("$.city").value("Białystok"))
                .andExpect(jsonPath("$.postalCode").value("15-001"))
                .andExpect(jsonPath("$.street").value("Młynowa"))
                .andExpect(jsonPath("$.buildingNumber").value("17"));
        verify(facilityService).getFacility(facilityId);
    }

    @Test
    void getAllFacilities_FacilityExists_FacilityReturned() throws Exception {
        //given
        Long facilityId = 1L;
        FacilityDto facilityDto = new FacilityDto(facilityId, "Przychodnia Młynowa", "Białystok", "15-001",
                "Młynowa", "17");
        PageDto<FacilityDto> pageDto = new PageDto<>(List.of(facilityDto), 0,10,1L,1);
        when(facilityService.getAllFacilities(any(Pageable.class))).thenReturn(pageDto);
        //when and then
        mockMvc.perform(get("/api/facilities")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Przychodnia Młynowa"))
                .andExpect(jsonPath("$.content[0].city").value("Białystok"))
                .andExpect(jsonPath("$.content[0].postalCode").value("15-001"))
                .andExpect(jsonPath("$.content[0].street").value("Młynowa"))
                .andExpect(jsonPath("$.content[0].buildingNumber").value("17"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(facilityService).getAllFacilities(pageableCaptor.capture());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(10, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void addFacility_ValidData_FacilityCreated() throws Exception {
        // given
        CreateFacilityCommand command = new CreateFacilityCommand("Przychodnia Młynowa", "Białystok", "15-001",
                "Młynowa", "17");
        FacilityDto facilityDto = new FacilityDto(1L, "Przychodnia Młynowa", "Białystok", "15-001",
                "Młynowa", "17");
        when(facilityService.addFacility(any(CreateFacilityCommand.class))).thenReturn(facilityDto);
        // when & then
        mockMvc.perform(post("/api/facilities")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Przychodnia Młynowa"))
                .andExpect(jsonPath("$.city").value("Białystok"))
                .andExpect(jsonPath("$.postalCode").value("15-001"))
                .andExpect(jsonPath("$.street").value("Młynowa"))
                .andExpect(jsonPath("$.buildingNumber").value("17"));
        ArgumentCaptor<CreateFacilityCommand> captor = ArgumentCaptor.forClass(CreateFacilityCommand.class);
        verify(facilityService).addFacility(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void updateFacility_FacilityExists_FacilityUpdated() throws Exception {
        // given
        Long facilityId = 1L;
        UpdateFacilityCommand command = new UpdateFacilityCommand("Przychodnia Młynowa", "Białystok", "15-001",
                "Młynowa", "17");
        FacilityDto facilityDto = new FacilityDto(facilityId, "Przychodnia Młynowa", "Białystok",
                "15-001", "Młynowa", "17");
        when(facilityService.updateFacility(eq(facilityId), any(UpdateFacilityCommand.class))).thenReturn(facilityDto);
        // when & then
        mockMvc.perform(put("/api/facilities/{id}", facilityId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Przychodnia Młynowa"))
                .andExpect(jsonPath("$.city").value("Białystok"))
                .andExpect(jsonPath("$.postalCode").value("15-001"))
                .andExpect(jsonPath("$.street").value("Młynowa"))
                .andExpect(jsonPath("$.buildingNumber").value("17"));
        ArgumentCaptor<UpdateFacilityCommand> captor = ArgumentCaptor.forClass(UpdateFacilityCommand.class);
        verify(facilityService).updateFacility(eq(facilityId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void deleteFacility_FacilityExists_FacilityDeleted() throws Exception {
        // given
        Long facilityId = 1L;
        doNothing().when(facilityService).deleteFacility(facilityId);
        // when & then
        mockMvc.perform(delete("/api/facilities/{id}", facilityId))
                .andExpect(status().isNoContent());
        verify(facilityService).deleteFacility(facilityId);
    }

    @Test
    void getFacility_FacilityDoesNotExist_NotFound() throws Exception {
        // given
        Long facilityId = 999L;
        when(facilityService.getFacility(facilityId)).thenThrow(new FacilityNotFoundException(facilityId));
        // when & then
        mockMvc.perform(get("/api/facilities/{id}", facilityId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(
                        "Facility not found with id: " + facilityId))
                .andExpect(jsonPath("$.timestamp").exists());
        verify(facilityService).getFacility(facilityId);
    }

    @Test
    void addFacility_FacilityAlreadyExists_Conflict() throws Exception {
        // given
        CreateFacilityCommand command = new CreateFacilityCommand("Przychodnia Młynowa", "Białystok", "15-001",
                "Młynowa", "17");
        when(facilityService.addFacility(any(CreateFacilityCommand.class))).thenThrow(new FacilityAlreadyExistsException(command.name()));
        // when & then
        mockMvc.perform(post("/api/facilities")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Facility already exists with name: Przychodnia Młynowa"));
        ArgumentCaptor<CreateFacilityCommand> captor = ArgumentCaptor.forClass(CreateFacilityCommand.class);
        verify(facilityService).addFacility(captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void updateFacility_FacilityDoesNotExist_NotFound() throws Exception {
        // given
        Long facilityId = 999L;
        UpdateFacilityCommand command = new UpdateFacilityCommand("Przychodnia Młynowa", "Białystok", "15-001",
                "Młynowa", "17");
        when(facilityService.updateFacility(eq(facilityId), any(UpdateFacilityCommand.class))).thenThrow(new FacilityNotFoundException(facilityId));
        // when & then
        mockMvc.perform(put("/api/facilities/{id}", facilityId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Facility not found with id: 999"));
        ArgumentCaptor<UpdateFacilityCommand> captor = ArgumentCaptor.forClass(UpdateFacilityCommand.class);
        verify(facilityService).updateFacility(eq(facilityId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void deleteFacility_FacilityDoesNotExist_NotFound() throws Exception {
        // given
        Long facilityId = 999L;
        doThrow(new FacilityNotFoundException(facilityId)).when(facilityService).deleteFacility(facilityId);
        // when & then
        mockMvc.perform(delete("/api/facilities/{id}", facilityId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Facility not found with id: 999"));
        verify(facilityService).deleteFacility(facilityId);
    }

    @Test
    void updateFacility_FacilityAlreadyExists_Conflict() throws Exception {
        // given
        Long facilityId = 1L;
        UpdateFacilityCommand command = new UpdateFacilityCommand("Przychodnia Młynowa", "Białystok", "15-001",
                "Młynowa", "17");
        when(facilityService.updateFacility(eq(facilityId), any(UpdateFacilityCommand.class))).thenThrow(new FacilityAlreadyExistsException(command.name()));
        // when & then
        mockMvc.perform(put("/api/facilities/{id}", facilityId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Facility already exists with name: Przychodnia Młynowa"));
        ArgumentCaptor<UpdateFacilityCommand> captor = ArgumentCaptor.forClass(UpdateFacilityCommand.class);
        verify(facilityService).updateFacility(eq(facilityId), captor.capture());
        assertEquals(command, captor.getValue());
    }

    @Test
    void getFacility_UnexpectedException_InternalServerError() throws Exception {
        // given
        Long facilityId = 1L;
        when(facilityService.getFacility(facilityId)).thenThrow(new RuntimeException("Unexpected error"));
        // when & then
        mockMvc.perform(get("/api/facilities/{id}", facilityId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message")
                        .value("Internal server error"))
                .andExpect(jsonPath("$.timestamp").exists());
        verify(facilityService).getFacility(facilityId);
    }
}


