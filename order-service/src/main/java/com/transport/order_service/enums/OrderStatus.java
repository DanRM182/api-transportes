package com.transport.order_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public enum OrderStatus {
    CREATED,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus nuevoStatus) {
        return switch (this) {
            case CREATED -> (nuevoStatus == IN_TRANSIT ||
                    nuevoStatus == CANCELLED);

            case IN_TRANSIT -> nuevoStatus == DELIVERED;

            case DELIVERED, CANCELLED -> false;
        };
    }
}
