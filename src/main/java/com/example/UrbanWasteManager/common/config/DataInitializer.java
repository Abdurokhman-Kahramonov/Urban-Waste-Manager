package com.example.UrbanWasteManager.common.config;

import com.example.UrbanWasteManager.user.entity.User;
import com.example.UrbanWasteManager.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Value("${app.default-admin.email:admin@urbanwaste.com}")
    private String adminEmail;

    @Value("${app.default-admin.password:admin123}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                log.info("Creating default admin user: {}", adminEmail);
                User admin = User.builder()
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .firstName("Default")
                        .lastName("Admin")
                        .role("ADMIN")
                        .build();
                userRepository.save(admin);
                log.info("Default admin user created successfully.");
            } else {
                log.info("Default admin user already exists.");
            }
        };
    }
}
