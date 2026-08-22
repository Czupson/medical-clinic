package com.Czupson.medical_clinic.model;

import com.Czupson.medical_clinic.exception.user.UserDataValidationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Patient patient;

    public void validate() {
        if (email == null || email.isBlank()) {
            throw new UserDataValidationException("Email cannot be empty");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new UserDataValidationException("Invalid email format");
        }

        if (password == null || password.isBlank()) {
            throw new UserDataValidationException("Password cannot be empty");
        }

        if (password.length() < 8) {
            throw new UserDataValidationException("Password must have at least 8 characters");
        }
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new UserDataValidationException("Password cannot be empty");
        }

        if (newPassword.length() < 8) {
            throw new UserDataValidationException("Password must have at least 8 characters");
        }
        this.password = newPassword;
    }

    public void update(User updatedUser) {
        updatedUser.validate();

        this.email = updatedUser.getEmail();
        this.password = updatedUser.getPassword();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
