package com.example.auth.config;

import com.example.auth.model.AuthUser;
import com.example.auth.model.UserRole;
import com.example.auth.repository.AuthUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DemoUserSeeder {
    @Bean
    CommandLineRunner seedUsers(AuthUserRepository users, PasswordEncoder passwordEncoder) {
        return args -> {
            seedIfMissing(users, passwordEncoder, "usuario@test.com", UserRole.USER);
            seedIfMissing(users, passwordEncoder, "admin@test.com", UserRole.ADMIN);
        };
    }

    private void seedIfMissing(
            AuthUserRepository users,
            PasswordEncoder passwordEncoder,
            String email,
            UserRole role) {
        if (!users.existsByEmailIgnoreCase(email)) {
            users.save(new AuthUser(email, passwordEncoder.encode("123"), role));
        }
    }
}