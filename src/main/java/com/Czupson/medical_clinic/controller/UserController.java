package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.patient.ChangePasswordCommand;
import com.Czupson.medical_clinic.dto.user.CreateUserCommand;
import com.Czupson.medical_clinic.dto.user.UpdateUserCommand;
import com.Czupson.medical_clinic.mapper.UserMapper;
import com.Czupson.medical_clinic.model.User;
import com.Czupson.medical_clinic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @Operation(summary = "Get all users", description = "Returns a list of all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    @GetMapping
    public Page<User> getAllUsers(Pageable pageable) {
        return service.getAllUsers(pageable);
    }

    @Operation(summary = "Get user by id", description = "Returns user with the specified id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    @Operation(summary = "Create user", description = "Creates a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody CreateUserCommand command) {
        return service.addUser(mapper.toUser(command));
    }

    @Operation(summary = "Update user", description = "Updates an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id,
                           @RequestBody UpdateUserCommand command) {
        return service.updateUser(id, mapper.toUser(command));
    }

    @Operation(summary = "Delete user", description = "Deletes user with the specified id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }

    @Operation(summary = "Change user password", description = "Changes user's password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/password")
    public void changePassword(@PathVariable Long id,
                               @RequestBody ChangePasswordCommand command) {
        service.changePassword(id, command.newPassword());
    }
}