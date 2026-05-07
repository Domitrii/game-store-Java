package com.example.gameplatform.config;

import com.example.gameplatform.model.User;
import com.example.gameplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail("test@example.com").isEmpty()) {
            User user = new User();
            user.setEmail("test@example.com");
            user.setName("Test User");
            user.setPasswordHash(passwordEncoder.encode("test123"));
            userRepository.save(user);
        }
    }
}
