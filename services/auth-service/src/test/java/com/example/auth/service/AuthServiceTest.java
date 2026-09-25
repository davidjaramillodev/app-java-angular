package com.example.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.example.auth.model.LoginRequest;
import com.example.auth.model.LoginResponse;
import com.example.auth.model.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authenticationManager, jwtEncoder, "auth-test");
    }

    @Test
    void loginAuthenticatesUserAndIssuesJwtWithRole() {
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(authentication.getAuthorities()).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(
                Jwt.withTokenValue("signed-token")
                        .header("alg", "HS256")
                        .subject("admin@test.com")
                        .claim("roles", List.of("ROLE_ADMIN"))
                        .build());

        LoginResponse response = authService.login(new LoginRequest("admin@test.com", "123"));

        assertEquals("signed-token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals("admin@test.com", response.username());
        assertEquals("ADMIN", response.role());
        assertEquals(3600, response.expiresIn());

        ArgumentCaptor<JwtEncoderParameters> parameters = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(parameters.capture());
        assertEquals("auth-test", parameters.getValue().getClaims().getIssuer().toString());
        assertEquals("admin@test.com", parameters.getValue().getClaims().getSubject());
        assertEquals(List.of("ROLE_ADMIN"), parameters.getValue().getClaims().getClaim("roles"));
    }

    @Test
    void loginDoesNotIssueTokenWhenCredentialsAreRejected() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("invalid credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("user@test.com", "wrong")));
        verify(jwtEncoder, never()).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void profileMapsSubjectAndRolesFromJwt() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject("usuario@test.com")
                .claim("roles", List.of("ROLE_USER"))
                .build();

        UserProfile profile = authService.profile(jwt);

        assertEquals("usuario@test.com", profile.username());
        assertEquals(List.of("ROLE_USER"), profile.roles());
    }
}