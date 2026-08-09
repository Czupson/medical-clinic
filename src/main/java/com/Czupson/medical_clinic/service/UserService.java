package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.dto.PageDto;
import com.Czupson.medical_clinic.dto.user.UserDto;
import com.Czupson.medical_clinic.exception.user.UserAlreadyExistsException;
import com.Czupson.medical_clinic.exception.user.UserNotFoundException;
import com.Czupson.medical_clinic.mapper.UserMapper;
import com.Czupson.medical_clinic.model.User;
import com.Czupson.medical_clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public PageDto<UserDto> getAllUsers(Pageable pageable) {
        return PageDto.from(
                userRepository.findAll(pageable)
                        .map(userMapper::toDto)
        );
    }

    public UserDto getUser(Long id) {
        return userMapper.toDto(findUser(id));
    }

    public User addUser(User user) {
        user.validate();
        validateUserDoesNotExist(user.getEmail());
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User user = findUser(id);
        validateUserEmailUniqueness(id, updatedUser.getEmail());
        user.update(updatedUser);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.delete(findUser(id));
    }

    public void changePassword(Long id, String newPassword) {
        User user = findUser(id);
        user.changePassword(newPassword);
        userRepository.save(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private void validateUserEmailUniqueness(Long userId, String email) {
        userRepository.findByEmail(email)
                .ifPresent(foundUser -> {
                    if (!foundUser.getId().equals(userId)) {
                        throw new UserAlreadyExistsException(email);
                    }
                });
    }

    private void validateUserDoesNotExist(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }
    }
}
