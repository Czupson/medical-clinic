package com.Czupson.medical_clinic.repository;

import com.Czupson.medical_clinic.model.Doctor;
import com.Czupson.medical_clinic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);
    boolean existsByUser(User user);
}