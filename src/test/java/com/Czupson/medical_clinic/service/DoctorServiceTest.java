package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.Czupson.medical_clinic.dto.doctor.DoctorDto;
import com.Czupson.medical_clinic.dto.doctor.UpdateDoctorCommand;
import com.Czupson.medical_clinic.exception.doctor.DoctorAlreadyExistsException;
import com.Czupson.medical_clinic.exception.doctor.DoctorNotFoundException;
import com.Czupson.medical_clinic.exception.facility.FacilitiesNotFoundException;
import com.Czupson.medical_clinic.exception.user.UserNotFoundException;
import com.Czupson.medical_clinic.mapper.DoctorMapper;
import com.Czupson.medical_clinic.model.Doctor;
import com.Czupson.medical_clinic.model.Facility;
import com.Czupson.medical_clinic.model.User;
import com.Czupson.medical_clinic.repository.DoctorRepository;
import com.Czupson.medical_clinic.repository.FacilityRepository;
import com.Czupson.medical_clinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {

    private DoctorService doctorService;
    private DoctorRepository doctorRepository;
    private UserRepository userRepository;
    private FacilityRepository facilityRepository;
    private DoctorMapper doctorMapper;

    @BeforeEach
    void setUp() {
        doctorRepository = mock(DoctorRepository.class);
        userRepository = mock(UserRepository.class);
        facilityRepository = mock(FacilityRepository.class);
        doctorMapper = mock(DoctorMapper.class);
        doctorService = new DoctorService(
                doctorRepository,
                userRepository,
                facilityRepository,
                doctorMapper
        );
    }

    @Test
    void getDoctor_DoctorExists_DoctorReturned() {
        // given
        Long doctorId = 1L;
        Doctor doctor = createDoctor(doctorId);
        DoctorDto doctorDto = new DoctorDto(doctorId, "Jan", "Kowalski", "Kardiolog");
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorMapper.toDto(doctor)).thenReturn(doctorDto);

        // when
        DoctorDto result = doctorService.getDoctor(doctorId);
        // then
        assertEquals(doctorDto, result);
        verify(doctorRepository).findById(doctorId);
        verify(doctorMapper).toDto(doctor);
    }

    @Test
    void getAllDoctors_DoctorsExist_DoctorsReturned() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Doctor doctor = createDoctor(1L);
        DoctorDto doctorDto = new DoctorDto(1L, "Jan", "Kowalski", "Kardiolog");
        Page<Doctor> doctorPage = new PageImpl<>(List.of(doctor), pageable, 1);
        when(doctorRepository.findAll(pageable)).thenReturn(doctorPage);
        when(doctorMapper.toDto(doctor)).thenReturn(doctorDto);
        // when
        PageDto<DoctorDto> result = doctorService.getAllDoctors(pageable);
        // then
        assertEquals(List.of(doctorDto), result.content());
        assertEquals(0, result.pageNumber());
        assertEquals(10, result.pageSize());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        verify(doctorRepository).findAll(pageable);
        verify(doctorMapper).toDto(doctor);
    }

    @Test
    void addDoctor_ValidCommand_DoctorCreated() {
        // given
        Long userId = 1L;
        Long facilityId = 1L;
        User user = createUser(userId);
        Facility facility = createFacility(facilityId);
        CreateDoctorCommand command = new CreateDoctorCommand(userId, Set.of(facilityId), "Jan", "Kowalski", "Kardiolog");
        Doctor doctor = createDoctor(1L, "Jan", "Kowalski", "Kardiolog", user, Set.of(facility));
        DoctorDto doctorDto = new DoctorDto(1L, "Jan", "Kowalski", "Kardiolog");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(doctorRepository.existsByUser(user)).thenReturn(false);
        when(facilityRepository.findAllById(command.facilityIds())).thenReturn(List.of(facility));
        when(doctorMapper.toDoctor(command)).thenReturn(doctor);
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(doctorMapper.toDto(doctor)).thenReturn(doctorDto);
        // when
        DoctorDto result = doctorService.addDoctor(command);
        // then
        assertEquals(doctorDto, result);
        verify(userRepository).findById(userId);
        verify(doctorRepository).existsByUser(user);
        verify(facilityRepository).findAllById(command.facilityIds());
        verify(doctorMapper).toDoctor(command);
        verify(doctorRepository).save(doctor);
        verify(doctorMapper).toDto(doctor);
    }

    @Test
    void addDoctor_DoctorAlreadyExists_ExceptionThrown() {
        // given
        Long userId = 1L;
        Long facilityId = 1L;
        User user = createUser(userId);
        CreateDoctorCommand command = new CreateDoctorCommand(userId, Set.of(facilityId), "Jan", "Kowalski", "Kardiolog");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(doctorRepository.existsByUser(user)).thenReturn(true);
        // when and then
        assertThrows(DoctorAlreadyExistsException.class, () -> doctorService.addDoctor(command));
        verify(userRepository).findById(userId);
        verify(doctorRepository).existsByUser(user);
        verifyNoInteractions(facilityRepository, doctorMapper);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void addDoctor_UserDoesNotExist_ExceptionThrown() {
        // given
        Long userId = 1L;
        Long facilityId = 1L;
        CreateDoctorCommand command = new CreateDoctorCommand(userId, Set.of(facilityId), "Jan", "Kowalski", "Kardiolog");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(UserNotFoundException.class, () -> doctorService.addDoctor(command));
        verify(userRepository).findById(userId);
        verifyNoInteractions(doctorRepository, facilityRepository, doctorMapper);
    }

    @Test
    void addDoctor_FacilityDoesNotExist_ExceptionThrown() {
        // given
        Long userId = 1L;
        Long facilityId = 1L;
        Long missingFacilityId = 2L;
        User user = createUser(userId);
        Facility facility = createFacility(facilityId);
        CreateDoctorCommand command = new CreateDoctorCommand(userId, Set.of(facilityId, missingFacilityId), "Jan", "Kowalski", "Kardiolog");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(doctorRepository.existsByUser(user)).thenReturn(false);
        when(facilityRepository.findAllById(command.facilityIds())).thenReturn(List.of(facility));
        // when and then
        assertThrows(FacilitiesNotFoundException.class, () -> doctorService.addDoctor(command));
        verify(userRepository).findById(userId);
        verify(doctorRepository).existsByUser(user);
        verify(facilityRepository).findAllById(command.facilityIds());
        verifyNoInteractions(doctorMapper);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void updateDoctor_ValidCommand_DoctorUpdated() {
        // given
        Long doctorId = 1L;
        Long facilityId = 1L;
        User user = createUser(1L);
        Facility facility = createFacility(facilityId);
        Doctor doctor = createDoctor(doctorId, "Jan", "Kowalski", "Kardiolog", user, Set.of(facility));
        UpdateDoctorCommand command = new UpdateDoctorCommand(Set.of(facilityId), "Adam", "Nowak", "Dermatolog");
        Doctor updatedDoctor = createDoctor(doctorId, "Adam", "Nowak", "Dermatolog", user, Set.of(facility));
        DoctorDto doctorDto = new DoctorDto(doctorId, "Adam", "Nowak", "Dermatolog");
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findAllById(command.facilityIds())).thenReturn(List.of(facility));
        when(doctorMapper.toDoctor(command)).thenReturn(updatedDoctor);
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(doctorMapper.toDto(doctor)).thenReturn(doctorDto);
        // when
        DoctorDto result = doctorService.updateDoctor(doctorId, command);
        // then
        assertEquals(doctorDto, result);
        verify(doctorRepository).findById(doctorId);
        verify(facilityRepository).findAllById(command.facilityIds());
        verify(doctorMapper).toDoctor(command);
        verify(doctorRepository).save(doctor);
        verify(doctorMapper).toDto(doctor);
    }

    @Test
    void updateDoctor_DoctorDoesNotExist_ExceptionThrown() {
        // given
        Long doctorId = 1L;
        Long facilityId = 1L;
        UpdateDoctorCommand command = new UpdateDoctorCommand(Set.of(facilityId), "Adam", "Nowak", "Dermatolog");
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(DoctorNotFoundException.class, () -> doctorService.updateDoctor(doctorId, command));
        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository, never()).save(any(Doctor.class));
        verifyNoInteractions(facilityRepository, doctorMapper);
    }

    @Test
    void updateDoctor_FacilityDoesNotExist_ExceptionThrown() {
        // given
        Long doctorId = 1L;
        Long facilityId = 1L;
        Long missingFacilityId = 2L;
        User user = createUser(1L);
        Doctor doctor = createDoctor(doctorId, "Jan", "Kowalski", "Kardiolog", user, Set.of());
        UpdateDoctorCommand command = new UpdateDoctorCommand(Set.of(facilityId, missingFacilityId), "Adam", "Nowak", "Dermatolog");
        Facility facility = createFacility(facilityId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findAllById(command.facilityIds())).thenReturn(List.of(facility));
        // when and then
        assertThrows(FacilitiesNotFoundException.class, () -> doctorService.updateDoctor(doctorId, command));
        verify(doctorRepository).findById(doctorId);
        verify(facilityRepository).findAllById(command.facilityIds());
        verifyNoInteractions(doctorMapper);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void deleteDoctor_DoctorExists_DoctorDeleted() {
        // given
        Long doctorId = 1L;
        Doctor doctor = createDoctor(doctorId, "Jan", "Kowalski", "Kardiolog", createUser(1L), Set.of(createFacility(1L)));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        // when
        doctorService.deleteDoctor(doctorId);
        // then
        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository).delete(doctor);
    }

    @Test
    void deleteDoctor_DoctorDoesNotExist_ExceptionThrown() {
        // given
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(DoctorNotFoundException.class, () -> doctorService.deleteDoctor(doctorId));
        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository, never()).delete(any(Doctor.class));
    }

    private Doctor createDoctor(Long doctorId) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setFirstName("Jan");
        doctor.setLastName("Kowalski");
        doctor.setSpecialization("Kardiolog");
        return doctor;
    }

    private User createUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("jan.kowalski@example.com");
        user.setPassword("password123");
        return user;
    }

    private Facility createFacility(Long facilityId) {
        Facility facility = new Facility();
        facility.setId(facilityId);
        facility.setName("Przychodnia Młynowa");
        facility.setCity("Białystok");
        facility.setPostalCode("15-404");
        facility.setStreet("Młynowa");
        facility.setBuildingNumber("17");
        return facility;
    }

    private Doctor createDoctor(
            Long doctorId,
            String firstName,
            String lastName,
            String specialization,
            User user,
            Set<Facility> facilities) {

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setSpecialization(specialization);
        doctor.setUser(user);
        doctor.setFacilities(new HashSet<>(facilities));

        return doctor;
    }
}