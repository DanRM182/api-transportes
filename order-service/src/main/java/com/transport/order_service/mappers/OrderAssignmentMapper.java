package com.transport.order_service.mappers;

import com.transport.order_service.dto.response.OrderAssignmentResponse;
import com.transport.order_service.entities.Order;
import com.transport.order_service.entities.OrderAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderAssignmentMapper {
    default OrderAssignment toEntity(Order order, UUID driverId) {
        return OrderAssignment.create(order, driverId);
    }


    @Mapping(
            target = "orderId",
            source = "order.id"
    )
    OrderAssignmentResponse toResponse(OrderAssignment assignment);
}