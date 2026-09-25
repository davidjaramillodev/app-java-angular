package com.example.auth.service;

import java.time.Instant;
import java.util.List;

import com.example.auth.model.LoginRequest;
import com.example.auth.model.LoginResponse;
import com.example.auth.model.UserProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final long TOKEN_LIFETIME_SECONDS = 3600;

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final String issuer;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            @Value("${app.security.jwt-issuer}") String issuer) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        Instant issuedAt = Instant.now();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(TOKEN_LIFETIME_SECONDS))
                .subject(authentication.getName())
                .claim("roles", roles)
                .build();
        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
        String role = roles.stream()
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .findFirst()
                .orElse("USER");
        return new LoginResponse(token, "Bearer", TOKEN_LIFETIME_SECONDS, authentication.getName(), role);
    }

    public UserProfile profile(Jwt jwt) {
        return new UserProfile(jwt.getSubject(), jwt.getClaimAsStringList("roles"));
    }
}