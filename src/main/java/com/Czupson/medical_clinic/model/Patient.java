package com.Czupson.medical_clinic.model;

import com.Czupson.medical_clinic.exception.PatientDataValidationException;
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

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String idCardNo;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phoneNumber;

    private LocalDate birthday;

    public void update(Patient updatedPatient) {
        updatedPatient.validate();
        this.firstName = updatedPatient.getFirstName();
        this.lastName = updatedPatient.getLastName();
        this.phoneNumber = updatedPatient.getPhoneNumber();
        this.birthday = updatedPatient.getBirthday();
        this.idCardNo = updatedPatient.getIdCardNo();
        this.password = updatedPatient.getPassword();
        this.email = updatedPatient.getEmail();
    }

    public void validate() {
        if (email == null || email.isBlank()) {
            throw new PatientDataValidationException("Email cannot be empty");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new PatientDataValidationException("Invalid email format");
        }

        if (password == null || password.isBlank()) {
            throw new PatientDataValidationException("Password cannot be empty");
        }

        if (password.length() < 8) {
            throw new PatientDataValidationException("Password must have at least 8 characters");
        }

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

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new PatientDataValidationException("Password cannot be empty");
        }

        if (newPassword.length() < 8) {
            throw new PatientDataValidationException("Password must have at least 8 characters");
        }
        this.password = newPassword;
    }
}
