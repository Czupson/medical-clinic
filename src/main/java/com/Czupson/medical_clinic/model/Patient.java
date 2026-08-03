package com.Czupson.medical_clinic.model;

import com.Czupson.medical_clinic.exception.patient.PatientDataValidationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String idCardNo;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phoneNumber;

    private LocalDate birthday;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    public void update(Patient updatedPatient) {
        updatedPatient.validate();
        this.firstName = updatedPatient.getFirstName();
        this.lastName = updatedPatient.getLastName();
        this.phoneNumber = updatedPatient.getPhoneNumber();
        this.birthday = updatedPatient.getBirthday();
        this.idCardNo = updatedPatient.getIdCardNo();
    }

    public void validate() {

        if (firstName == null || firstName.isBlank()) {
            throw new PatientDataValidationException("First name cannot be empty");
        }

        if (!firstName.matches("^[A-Za-zĄąĆćĘęŁłŃńÓóŚśŹźŻż-]+$")) {
            throw new PatientDataValidationException("Invalid first name");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new PatientDataValidationException("Last name cannot be empty");
        }

        if (!lastName.matches("^[A-Za-zĄąĆćĘęŁłŃńÓóŚśŹźŻż-]+$")) {
            throw new PatientDataValidationException("Invalid last name");
        }

        if (idCardNo == null || idCardNo.isBlank()) {
            throw new PatientDataValidationException("ID card number cannot be empty");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new PatientDataValidationException("Phone number cannot be empty");
        }

        if (!phoneNumber.matches("^\\+?[0-9]{9,15}$")) {
            throw new PatientDataValidationException("Invalid phone number format");
        }

        if (birthday == null) {
            throw new PatientDataValidationException("Birthday cannot be empty");
        }

        if (birthday.isAfter(LocalDate.now())) {
            throw new PatientDataValidationException("Birthday cannot be in the future");
        }
    }
}
