package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.user.UserDto;
import com.Czupson.medical_clinic.exception.user.UserAlreadyExistsException;
import com.Czupson.medical_clinic.exception.user.UserNotFoundException;
import com.Czupson.medical_clinic.mapper.UserMapper;
import com.Czupson.medical_clinic.model.User;
import com.Czupson.medical_clinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        userService = new UserService(
                userRepository,
                userMapper
        );
    }

    @Test
    void getUser_UserExists_UserReturned() {
        // given
        Long userId = 1L;
        User user = createUser(userId);
        UserDto userDto = new UserDto(userId, "jan.kowalski@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        // when
        UserDto result = userService.getUser(userId);
        // then
        assertEquals(userDto, result);
        verify(userRepository).findById(userId);
        verify(userMapper).toDto(user);
    }

    @Test
    void getAllUsers_UsersExist_UsersReturned() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        User user = createUser(1L);
        UserDto userDto = new UserDto(1L, "jan.kowalski@example.com");
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(userDto);
        // when
        PageDto<UserDto> result = userService.getAllUsers(pageable);
        // then
        assertEquals(List.of(userDto), result.content());
        assertEquals(0, result.pageNumber());
        assertEquals(10, result.pageSize());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        verify(userRepository).findAll(pageable);
        verify(userMapper).toDto(user);
    }

    @Test
    void addUser_ValidUser_UserCreated() {
        // given
        User user = createUser(1L);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        // when
        User result = userService.addUser(user);
        // then
        assertEquals(user, result);
        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void addUser_UserAlreadyExists_ExceptionThrown() {
        // given
        User user = createUser(1L);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        // when and then
        assertThrows(UserAlreadyExistsException.class, () -> userService.addUser(user));
        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(user);
    }

    @Test
    void updateUser_ValidUser_UserUpdated() {
        // given
        Long userId = 1L;
        User user = createUser(userId);
        User updatedUser = createUser(userId);
        updatedUser.setEmail("adam.nowak@example.com");
        updatedUser.setPassword("newpassword123");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(updatedUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        // when
        User result = userService.updateUser(userId, updatedUser);
        // then
        assertEquals(user, result);
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(updatedUser.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_EmailBelongsToAnotherUser_ExceptionThrown() {
        // given
        Long userId = 1L;
        User user = createUser(userId);
        User updatedUser = createUser(userId);
        updatedUser.setEmail("adam.nowak@example.com");
        updatedUser.setPassword("newpassword123");
        User existingUser = createUser(2L);
        existingUser.setEmail("adam.nowak@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(updatedUser.getEmail())).thenReturn(Optional.of(existingUser));
        // when and then
        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(userId, updatedUser));
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(updatedUser.getEmail());
        verify(userRepository, never()).save(user);
    }

    @Test
    void getUser_UserDoesNotExist_ExceptionThrown() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUser_UserDoesNotExist_ExceptionThrown() {
        // given
        Long userId = 1L;
        User updatedUser = createUser(userId);
        updatedUser.setEmail("adam.nowak@example.com");
        updatedUser.setPassword("newpassword123");
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        // when and then
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, updatedUser));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_UserExists_UserDeleted() {
        // given
        Long userId = 1L;
        User user = createUser(userId);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        // when
        userService.deleteUser(userId);
        // then
        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_UserDoesNotExist_ExceptionThrown() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        // when and then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void changePassword_UserExists_PasswordChanged() {
        // given
        Long userId = 1L;
        String newPassword = "newPassword123";
        User user = createUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        // when
        userService.changePassword(userId, newPassword);
        // then
        assertEquals(newPassword, user.getPassword());
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_UserDoesNotExist_ExceptionThrown() {
        // given
        Long userId = 1L;
        String newPassword = "newPassword123";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        // when and then
        assertThrows(UserNotFoundException.class, () -> userService.changePassword(userId, newPassword));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    private User createUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("jan.kowalski@example.com");
        user.setPassword("password123");
        return user;
    }
}
