package com.transport.order_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @Schema(
                description = "Origen de la orden",
                example = "C. Independencia 684, Zona Centro, 44100 Guadalajara, Jal."
        )
        @NotBlank(message = "El origen es requerido")
        @Size(max = 255, message = "El origen debe tener máximo 255 caracteres")
        String origin,

        @Schema(
                description = "Destino de la orden",
                example = "Av. Juárez S/N, Centro Histórico de la Cdad. de México, Centro, Cuauhtémoc, 06050 Ciudad de México, CDMX"
        )
        @NotBlank(message = "El destino es requerido")
        @Size(max = 255, message = "El destino debe tener máximo 255 caracteres")
        String destination
) { }