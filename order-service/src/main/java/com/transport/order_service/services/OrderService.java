package com.transport.order_service.services;

import com.transport.order_service.dto.request.ChangeOrderStatusRequest;
import com.transport.order_service.dto.request.CreateOrderRequest;
import com.transport.order_service.dto.request.OrderFilterRequest;
import com.transport.order_service.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(CreateOrderRequest request);

    OrderResponse findOrder(UUID id);

    OrderResponse changeStatus(UUID id, ChangeOrderStatusRequest request);

    List<OrderResponse> listOrdersByFilter(OrderFilterRequest filters);
}
