package com.transport.order_service.repositories;

import com.transport.order_service.entities.OrderAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderAssignmentRepository extends JpaRepository<OrderAssignment, UUID> {
    boolean existsByOrderId(UUID orderId);
}
