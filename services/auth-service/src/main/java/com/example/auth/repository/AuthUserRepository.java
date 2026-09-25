package com.example.auth.repository;

import java.util.Optional;

import com.example.auth.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}