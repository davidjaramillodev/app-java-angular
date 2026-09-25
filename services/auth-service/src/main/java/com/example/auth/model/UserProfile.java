package com.example.auth.model;

import java.util.List;

public record UserProfile(String username, List<String> roles) {
}