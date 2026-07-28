package com.Czupson.medical_clinic.service;

import com.Czupson.medical_clinic.exception.UserAlreadyExistsException;
import com.Czupson.medical_clinic.exception.UserNotFoundException;
import com.Czupson.medical_clinic.model.User;
import com.Czupson.medical_clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User addUser(User user) {
        user.validate();
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail());
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.findByEmail(updatedUser.getEmail())
                .ifPresent(foundUser -> {
                    if (!foundUser.getId().equals(id)) {
                        throw new UserAlreadyExistsException(updatedUser.getEmail());
                    }
                });
        user.update(updatedUser);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }

    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.changePassword(newPassword);
        userRepository.save(user);
    }
}
