package com.Czupson.medical_clinic.model;

import com.Czupson.medical_clinic.exception.DoctorDataValidationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Facility facility;

    public void update(Doctor updatedDoctor) {
        updatedDoctor.validate();
        this.firstName = updatedDoctor.getFirstName();
        this.lastName = updatedDoctor.getLastName();
        this.specialization = updatedDoctor.getSpecialization();
        this.facility = updatedDoctor.getFacility();
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
        if (facility == null) {
            throw new DoctorDataValidationException("Doctor must belong to a facility");
        }
    }
}
