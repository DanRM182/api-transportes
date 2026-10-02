package com.transport.order_service.dto.request;

import com.transport.order_service.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ChangeOrderStatusRequest(
        @Schema(
                description = "Estado de la orden",
                example = "IN_TRANSIT"
        )
        @NotNull(message = "El estado de la orden es requerido")
        OrderStatus status
) { }