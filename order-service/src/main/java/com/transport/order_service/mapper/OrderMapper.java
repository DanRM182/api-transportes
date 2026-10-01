package com.transport.order_service.mapper;

import com.transport.order_service.dto.request.ChangeOrderStatusRequest;
import com.transport.order_service.dto.request.CreateOrderRequest;
import com.transport.order_service.dto.response.OrderResponse;
import com.transport.order_service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

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
