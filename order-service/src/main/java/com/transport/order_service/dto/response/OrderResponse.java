package com.transport.order_service.dto.response;

import com.transport.order_service.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        @Schema(
                description = "Identificador único de la orden",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID id,

        @Schema(
                description = "Estado de la orden",
                example = "CREATED"
        )
        OrderStatus status,

        @Schema(
                description = "Origen de la orden",
                example = "C. Independencia 684, Zona Centro, 44100 Guadalajara, Jal."
        )
        String origin,

        @Schema(
                description = "Destino de la orden",
                example = "Av. Juárez S/N, Centro Histórico de la Cdad. de México, Centro, Cuauhtémoc, 06050 Ciudad de México, CDMX"
        )
        String destination,

        @Schema(
                description = "Fecha de creación de la orden",
                example = "2026-10-01T20:30:00Z"
        )
        Instant createdAt,

        @Schema(
                description = "Fecha de actualización de la orden",
                example = "2026-12-01T20:30:00Z"
        )
        Instant updatedAt
) { }