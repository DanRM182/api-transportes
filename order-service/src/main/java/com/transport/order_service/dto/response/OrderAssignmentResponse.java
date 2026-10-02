package com.transport.order_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record OrderAssignmentResponse(
        @Schema(
                description = "Identificador único de la asignación de la orden",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID id,

        @Schema(
                description = "Identificador único de la orden",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID orderId,

        @Schema(
                description = "Identificador único del conductor",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID driverId,

        @Schema(
                description = "Dirección de almacenamiento del archivo PDF",
                example = "/example/example.pdf"
        )
        String pdfPath,

        @Schema(
                description = "Dirección de almacenamiento de la imagen",
                example = "/example/example.png"
        )
        String imagePaths
) { }