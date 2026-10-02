package com.transport.order_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record DriverClientResponse(
        @Schema(
                description = "Identificador único del conductor",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID id,

        @Schema(
                description = "Nombre completo del conductor",
                example = "Juan Carlos Torres Melgar"
        )
        String name,

        @Schema(
                description = "Número de licencia del conductor",
                example = "2332ACCRT1"
        )
        String licenseNumber,

        @Schema(
                description = "Estado del conductor",
                example = "true"
        )
        boolean active
) {}