package com.transport.order_service.auth;

import com.transport.order_service.auth.dto.LoginRequest;
import com.transport.order_service.auth.dto.LoginResponse;
import com.transport.order_service.security.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final String configuredUsername;
    private final String configuredPassword;

    public AuthController(
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.username}") String configuredUsername,
            @Value("${app.security.password}") String configuredPassword) {

        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.configuredUsername = configuredUsername;
        this.configuredPassword = configuredPassword;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Autenticación",
            description = "Autentica al usuario y genera un token JWT",
            security = {}
    )
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        if (!configuredUsername.equals(request.username())
                || !passwordEncoder.matches(
                request.password(),
                configuredPassword)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        String token = jwtService.generateToken(request.username());

        return ResponseEntity.ok(
                new LoginResponse(token, "Bearer"));
    }
}