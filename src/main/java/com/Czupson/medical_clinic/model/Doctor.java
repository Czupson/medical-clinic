package com.Czupson.medical_clinic.model;

import com.Czupson.medical_clinic.exception.DoctorDataValidationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String specialization;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "doctor_facility",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<Facility> facilities = new HashSet<>();

    public void update(Doctor updatedDoctor) {
        updatedDoctor.validate();
        this.firstName = updatedDoctor.getFirstName();
        this.lastName = updatedDoctor.getLastName();
        this.specialization = updatedDoctor.getSpecialization();
        this.facilities = new HashSet<>(updatedDoctor.getFacilities());
    }

    public void validate() {
        if (firstName == null || firstName.isBlank()) {
            throw new DoctorDataValidationException("First name cannot be empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new DoctorDataValidationException("Last name cannot be empty");
        }
        if (specialization == null || specialization.isBlank()) {
            throw new DoctorDataValidationException("Specialization cannot be empty");
        }
        if (user == null) {
            throw new DoctorDataValidationException("Doctor must have a user");
        }
        if (facilities == null || facilities.isEmpty()) {
            throw new DoctorDataValidationException(
                    "Doctor must be assigned to at least one facility");
        }
    }
}
