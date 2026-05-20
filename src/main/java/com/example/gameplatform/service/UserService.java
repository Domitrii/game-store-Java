package com.example.gameplatform.service;

import com.example.gameplatform.model.User;
import com.example.gameplatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired private UserRepository userRepository;

    @Transactional
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Registration failed — email already exists: {}", user.getEmail());
            throw new RuntimeException("Email already registered: " + user.getEmail());
        }
        log.info("Registering new user: {}", user.getEmail());
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        log.info("Looking up user by email: {}", email);
        return userRepository.findByEmail(email);
    }
}
