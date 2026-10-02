package com.transport.order_service.entity;

import com.transport.order_service.common.exception.IllegalOrderStatusTransition;
import com.transport.order_service.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity @Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false, length = 255)
    private String origin;

    @Column(nullable = false, length = 255)
    private String destination;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private static String validateString(String data, String message) {
        if(data == null || data.isBlank())
            throw new IllegalArgumentException(message);

        return data.trim();
    }

    private void validateOrderTransitions(OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "El nuevo status de la orden es requerido"
            );
        }

        if(!this.status.canTransitionTo(newStatus))
            throw new IllegalOrderStatusTransition(
                    "No se puede pasar de status: " + this.status.name()
            + " a " + newStatus.name());
    }

    public Order(String origin, String destination) {
        this.status = OrderStatus.CREATED;

        this.origin = validateString(origin,
                "El origen de la órden es requerido");

        this.destination = validateString(destination,
                "el destino de la órden es requerido");
    }

    public static Order create(String origin, String destination) {
        return new Order(origin, destination);
    }

    public void updateOrderStatus(OrderStatus newStatus) {
        validateOrderTransitions(newStatus);

        this.status = newStatus;
    }

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
