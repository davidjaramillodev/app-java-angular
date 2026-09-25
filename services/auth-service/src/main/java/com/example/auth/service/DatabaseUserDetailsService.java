package com.example.auth.service;

import com.example.auth.model.AuthUser;
import com.example.auth.repository.AuthUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final AuthUserRepository users;

    public DatabaseUserDetailsService(AuthUserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthUser account = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return User.withUsername(account.getEmail())
                .password(account.getPasswordHash())
                .authorities("ROLE_" + account.getRole().name())
                .build();
    }
}