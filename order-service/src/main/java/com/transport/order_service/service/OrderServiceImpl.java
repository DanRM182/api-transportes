package com.transport.order_service.service;

import com.transport.order_service.common.exception.ResourceNotFoundException;
import com.transport.order_service.dto.request.ChangeOrderStatusRequest;
import com.transport.order_service.dto.request.CreateOrderRequest;
import com.transport.order_service.dto.request.OrderFilterRequest;
import com.transport.order_service.dto.response.OrderResponse;
import com.transport.order_service.entity.Order;
import com.transport.order_service.mapper.OrderMapper;
import com.transport.order_service.repository.OrderRepository;
import com.transport.order_service.specification.OrderSpecifications;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse create(CreateOrderRequest request) {
        log.info("Registrando nueva orden");

        Order order = orderMapper.toEntity(request);

        Order savedOrder = orderRepository.save(order);

        log.info("Nueva orden registrada con id: {}", savedOrder.getId());

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findOrder(UUID id) {
        log.info("Buscando orden por id {}", id);

        return orderMapper.toResponse(findOrderById(id));
    }

    @Override
    public OrderResponse changeStatus(UUID id, ChangeOrderStatusRequest request) {
        log.info("Buscando orden por id {}", id);

        Order order = findOrderById(id);

        order.updateOrderStatus(request.status());

        Order updatedOrder = orderRepository.save(order);

        log.info("Estado de orden actualizada a: {}", updatedOrder.getStatus());

        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrdersByFilter(OrderFilterRequest filters) {
        log.info("Buscando orden por filtros");

        validateDateRange(filters);

        Specification<Order> specification = OrderSpecifications.filterOrders(filters);

        return orderRepository.findAll(specification).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    private Order findOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Orden no encontrada con id: " + id));
    }

    private void validateDateRange(OrderFilterRequest filters) {
        if (filters.createdFrom() != null
                && filters.createdTo() != null
                && filters.createdFrom().isAfter(filters.createdTo())) {

            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser posterior a la fecha final");
        }
    }
}
