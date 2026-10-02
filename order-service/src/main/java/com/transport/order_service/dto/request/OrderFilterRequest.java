package com.transport.order_service.dto.request;

import com.transport.order_service.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record OrderFilterRequest(
        @Schema(
                description = "Estado de la orden",
                example = "IN_TRANSIT"
        )
        OrderStatus status,

        @Schema(
                description = "Origen de la orden",
                example = "Guadalajara"
        )
        String origin,

        @Schema(
                description = "Destino de la orden",
                example = "Ciudad de México"
        )
        String destination,
        @Schema(
                description = "Rango inicial fecha de creación de la orden",
                example = "2026-10-01T20:30:00Z"
        )
        Instant createdFrom,

        @Schema(
                description = "Rango final fecha de creación de la orden",
                example = "2026-12-01T20:30:00Z"
        )
        Instant createdTo
) {}