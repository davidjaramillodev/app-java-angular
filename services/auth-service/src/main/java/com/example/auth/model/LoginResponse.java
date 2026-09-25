package com.example.auth.model;

public record LoginResponse(String accessToken, String tokenType, long expiresIn, String username, String role) {
}