package com.transport.order_service.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "Usuario para autenticación", example = "admin")
        @NotBlank(message = "El usuario es requerido")
        String username,

        @Schema(description = "Contraseña para autenticación", example = "Admin123@")
        @NotBlank(message = "La contraseña es requerida")
        String password
) { }