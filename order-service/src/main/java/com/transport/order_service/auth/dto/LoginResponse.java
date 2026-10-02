package com.transport.order_service.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "Token JWT generado para autenticación")
        String token,

        @Schema(description = "Tipo de autenticación", example = "Bearer")
        String tokenType
) { }