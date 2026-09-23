package com.Czupson.medical_clinic.dto.user;

public record CreateUserCommand(
        String email,
        String password
) {
}