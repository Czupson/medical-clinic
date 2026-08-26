package com.Czupson.medical_clinic.controller;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.user.UpdateUserCommand;
import com.Czupson.medical_clinic.dto.user.UserDto;
import com.Czupson.medical_clinic.dto.user.CreateUserCommand;
import com.Czupson.medical_clinic.exception.user.UserAlreadyExistsException;
import com.Czupson.medical_clinic.exception.user.UserDataValidationException;
import com.Czupson.medical_clinic.exception.user.UserNotFoundException;
import com.Czupson.medical_clinic.model.User;
import com.Czupson.medical_clinic.mapper.UserMapper;
import com.Czupson.medical_clinic.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    void getUser_UserExists_UserReturned() throws Exception {
        // given
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "jan.kowalski@example.com");
        when(userService.getUser(userId)).thenReturn(userDto);
        // when and then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email")
                        .value("jan.kowalski@example.com"));
        verify(userService).getUser(userId);
    }

    @Test
    void getAllUsers_UsersExist_UsersReturned() throws Exception {
        // given
        UserDto userDto = new UserDto(1L, "jan.kowalski@example.com");
        PageDto<UserDto> pageDto = new PageDto<>(List.of(userDto), 0, 10, 1, 1);
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(pageDto);
        // when and then
        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email")
                        .value("jan.kowalski@example.com"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
        verify(userService).getAllUsers(any(Pageable.class));
    }

    @Test
    void addUser_ValidCommand_UserCreated() throws Exception {
        // given
        CreateUserCommand command = new CreateUserCommand(
                "jan.kowalski@example.com",
                "password123"
        );
        User user = new User();
        user.setId(1L);
        user.setEmail("jan.kowalski@example.com");
        user.setPassword("password123");
        when(userMapper.toUser(any(CreateUserCommand.class))).thenReturn(user);
        when(userService.addUser(user)).thenReturn(user);
        String requestBody = """
            {
                "email": "jan.kowalski@example.com",
                "password": "password123"
            }
            """;
        // when and then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email")
                        .value("jan.kowalski@example.com"));
        verify(userMapper).toUser(any(CreateUserCommand.class));
        verify(userService).addUser(user);
    }

    @Test
    void updateUser_ValidCommand_UserUpdated() throws Exception {
        // given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("adam.nowak@example.com");
        user.setPassword("password123");
        when(userMapper.toUser(any(UpdateUserCommand.class))).thenReturn(user);
        when(userService.updateUser(userId, user)).thenReturn(user);
        String requestBody = """
            {
                "email": "adam.nowak@example.com"
            }
            """;
        // when and then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email")
                        .value("adam.nowak@example.com"));
        verify(userMapper).toUser(any(UpdateUserCommand.class));
        verify(userService).updateUser(userId, user);
    }

    @Test
    void deleteUser_UserExists_UserDeleted() throws Exception {
        // given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);
        // when and then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(userService).deleteUser(userId);
    }

    @Test
    void changePassword_ValidCommand_PasswordChanged() throws Exception {
        // given
        Long userId = 1L;
        doNothing().when(userService).changePassword(userId, "newPassword123");
        String requestBody = """
            {
                "newPassword": "newPassword123"
            }
            """;
        // when and then
        mockMvc.perform(patch("/api/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(userService).changePassword(userId, "newPassword123");
    }

    @Test
    void getUser_UserDoesNotExist_NotFound() throws Exception {
        // given
        Long userId = 1L;
        when(userService.getUser(userId)).thenThrow(new UserNotFoundException(userId));
        // when and then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        verify(userService).getUser(userId);
    }

    @Test
    void addUser_InvalidData_BadRequest() throws Exception {
        // given
        User user = new User();
        user.setEmail("invalid-email");
        user.setPassword("123");
        when(userMapper.toUser(any(CreateUserCommand.class))).thenReturn(user);
        when(userService.addUser(user)).thenThrow(new UserDataValidationException("Invalid email format"));
        String requestBody = """
            {
                "email": "invalid-email",
                "password": "123"
            }
            """;
        // when and then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email format"));
        verify(userMapper).toUser(any(CreateUserCommand.class));
        verify(userService).addUser(user);
    }

    @Test
    void addUser_UserAlreadyExists_Conflict() throws Exception {
        // given
        User user = new User();
        user.setEmail("jan.kowalski@example.com");
        user.setPassword("password123");
        when(userMapper.toUser(any(CreateUserCommand.class))).thenReturn(user);
        when(userService.addUser(user)).thenThrow(new UserAlreadyExistsException("jan.kowalski@example.com"));
        String requestBody = """
            {
                "email": "jan.kowalski@example.com",
                "password": "password123"
            }
            """;
        // when and then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        verify(userMapper).toUser(any(CreateUserCommand.class));
        verify(userService).addUser(user);
    }

    @Test
    void updateUser_UserDoesNotExist_NotFound() throws Exception {
        // given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("adam.nowak@example.com");
        user.setPassword("password123");
        when(userMapper.toUser(any(UpdateUserCommand.class))).thenReturn(user);
        when(userService.updateUser(userId, user)).thenThrow(new UserNotFoundException(userId));
        String requestBody = """
            {
                "email": "adam.nowak@example.com"
            }
            """;
        // when and then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        verify(userMapper).toUser(any(UpdateUserCommand.class));
        verify(userService).updateUser(userId, user);
    }

    @Test
    void updateUser_InvalidData_BadRequest() throws Exception {
        // given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("invalid-email");
        user.setPassword("password123");
        when(userMapper.toUser(any(UpdateUserCommand.class))).thenReturn(user);
        when(userService.updateUser(userId, user)).thenThrow(new UserDataValidationException("Invalid email format"));
        String requestBody = """
            {
                "email": "invalid-email"
            }
            """;
        // when and then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email format"));
        verify(userMapper).toUser(any(UpdateUserCommand.class));
        verify(userService).updateUser(userId, user);
    }

    @Test
    void updateUser_UserAlreadyExists_Conflict() throws Exception {
        // given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("jan.kowalski@example.com");
        user.setPassword("password123");
        when(userMapper.toUser(any(UpdateUserCommand.class))).thenReturn(user);
        when(userService.updateUser(userId, user)).thenThrow(new UserAlreadyExistsException("jan.kowalski@example.com"));
        String requestBody = """
            {
                "email": "jan.kowalski@example.com"
            }
            """;
        // when and then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("User already exists with email: jan.kowalski@example.com"));
        verify(userMapper).toUser(any(UpdateUserCommand.class));
        verify(userService).updateUser(userId, user);
    }

    @Test
    void deleteUser_UserDoesNotExist_NotFound() throws Exception {
        // given
        Long userId = 1L;
        doThrow(new UserNotFoundException(userId)).when(userService).deleteUser(userId);
        // when and then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        verify(userService).deleteUser(userId);
    }

    @Test
    void changePassword_InvalidPassword_BadRequest() throws Exception {
        // given
        Long userId = 1L;
        doThrow(new UserDataValidationException("Password must have at least 8 characters")).when(userService).changePassword(userId, "123");
        String requestBody = """
            {
                "newPassword": "123"
            }
            """;
        // when and then
        mockMvc.perform(patch("/api/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Password must have at least 8 characters"));
        verify(userService).changePassword(userId, "123");
    }

    @Test
    void changePassword_UserDoesNotExist_NotFound() throws Exception {
        // given
        Long userId = 1L;
        doThrow(new UserNotFoundException(userId)).when(userService).changePassword(userId, "newPassword123");
        String requestBody = """
            {
                "newPassword": "newPassword123"
            }
            """;
        // when and then
        mockMvc.perform(patch("/api/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        verify(userService).changePassword(userId, "newPassword123");
    }
}