package com.transport.order_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignDriverRequest(
        @Schema(
                description = "Id del conductor",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @NotNull(message = "El conductor es requerido")
        UUID driverId
) { }