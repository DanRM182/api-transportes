package com.transport.order_service.mappers;

import com.transport.order_service.dto.request.CreateOrderRequest;
import com.transport.order_service.dto.response.OrderResponse;
import com.transport.order_service.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    default Order toEntity(CreateOrderRequest request) {
        return Order.create(
                request.origin(),
                request.destination()
        );
    }

    OrderResponse toResponse(Order order);
}
