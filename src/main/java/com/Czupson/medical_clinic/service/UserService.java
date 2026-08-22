package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.user.UserDto;
import com.Czupson.medical_clinic.exception.user.UserAlreadyExistsException;
import com.Czupson.medical_clinic.exception.user.UserNotFoundException;
import com.Czupson.medical_clinic.mapper.UserMapper;
import com.Czupson.medical_clinic.model.User;
import com.Czupson.medical_clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public PageDto<UserDto> getAllUsers(Pageable pageable) {
        return PageDto.from(
                userRepository.findAll(pageable)
                        .map(userMapper::toDto)
        );
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        return userMapper.toDto(findUser(id));
    }

    @Transactional
    public User addUser(User user) {
        log.info("Creating user");
        user.validate();
        validateUserDoesNotExist(user.getEmail());
        User savedUser = userRepository.save(user);
        log.info("User created: id={}", savedUser.getId());
        return savedUser;
    }
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        log.info("Updating user: id={}", id);
        User user = findUser(id);
        validateUserEmailUniqueness(id, updatedUser.getEmail());
        user.update(updatedUser);
        User savedUser = userRepository.save(user);
        log.info("User updated: id={}", savedUser.getId());
        return savedUser;
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user: id={}", id);
        userRepository.delete(findUser(id));
        log.info("User deleted: id={}", id);
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        log.info("Changing password for user: id={}", id);
        User user = findUser(id);
        user.changePassword(newPassword);
        userRepository.save(user);
        log.info("Password changed for user: id={}", id);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                        log.warn("User not found: id={}", id);
                        return new UserNotFoundException(id);
                });
    }

    private void validateUserEmailUniqueness(Long userId, String email) {
        userRepository.findByEmail(email)
                .ifPresent(foundUser -> {
                    if (!foundUser.getId().equals(userId)) {
                        log.warn("Attempt to assign already existing email to user: userId={}", userId);
                        throw new UserAlreadyExistsException(email);
                    }
                });
    }

    private void validateUserDoesNotExist(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("Attempt to create user that already exists");
            throw new UserAlreadyExistsException(email);
        }
    }
}
