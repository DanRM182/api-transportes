package com.transport.order_service.services;

import com.transport.order_service.common.exception.IllegalOrderStatusTransition;
import com.transport.order_service.common.exception.ResourceNotFoundException;
import com.transport.order_service.dto.request.ChangeOrderStatusRequest;
import com.transport.order_service.dto.request.CreateOrderRequest;
import com.transport.order_service.dto.request.OrderFilterRequest;
import com.transport.order_service.dto.response.OrderResponse;
import com.transport.order_service.entities.Order;
import com.transport.order_service.enums.OrderStatus;
import com.transport.order_service.mappers.OrderMapper;
import com.transport.order_service.repositories.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID id;

    private Order order;

    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        order = Order.create(
                "Guadalajara",
                "Ciudad de México"
        );

        ReflectionTestUtils.setField(order, "id", id);

        orderResponse = new OrderResponse(
                id,
                OrderStatus.CREATED,
                "Guadalajara",
                "Ciudad de México",
                null,
                null
        );
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        CreateOrderRequest request =
                new CreateOrderRequest(
                        "Guadalajara",
                        "Ciudad de México"
                );

        when(orderMapper.toEntity(request))
                .thenReturn(order);

        when(orderRepository.save(order))
                .thenReturn(order);

        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        OrderResponse result =
                orderService.create(request);

        assertEquals(orderResponse, result);

        verify(orderMapper).toEntity(request);

        verify(orderRepository).save(order);

        verify(orderMapper).toResponse(order);
    }

    @Test
    void shouldFindOrderWhenIdExists() {
        when(orderRepository.findById(id))
                .thenReturn(Optional.of(order));

        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        OrderResponse result =
                orderService.findOrder(id);

        assertEquals(orderResponse, result);

        verify(orderRepository).findById(id);

        verify(orderMapper).toResponse(order);
    }


    @Test
    void shouldThrowResourceNotFoundWhenOrderDoesNotExist() {
        when(orderRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findOrder(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Orden no encontrada con id: " + id);

        verify(orderRepository).findById(id);

        verifyNoInteractions(orderMapper);
    }

    @Test
    void shouldChangeOrderStatusSuccessfully() {
        ChangeOrderStatusRequest request =
                new ChangeOrderStatusRequest(
                        OrderStatus.IN_TRANSIT
                );

        OrderResponse updatedResponse =
                new OrderResponse(
                        id,
                        OrderStatus.IN_TRANSIT,
                        "Guadalajara",
                        "Ciudad de México",
                        null,
                        null
                );

        when(orderRepository.findById(id))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        when(orderMapper.toResponse(order))
                .thenReturn(updatedResponse);

        OrderResponse result =
                orderService.changeStatus(id, request);

        assertEquals(
                OrderStatus.IN_TRANSIT,
                order.getStatus()
        );

        assertEquals(updatedResponse, result);

        verify(orderRepository).findById(id);

        verify(orderRepository).save(order);

        verify(orderMapper).toResponse(order);
    }


    @Test
    void shouldRejectInvalidStatusTransition() {
        ChangeOrderStatusRequest request =
                new ChangeOrderStatusRequest(
                        OrderStatus.DELIVERED
                );

        when(orderRepository.findById(id))
                .thenReturn(Optional.of(order));

        assertThatThrownBy(
                () -> orderService.changeStatus(id, request)
        )
                .isInstanceOf(
                        IllegalOrderStatusTransition.class
                );

        assertEquals(
                OrderStatus.CREATED,
                order.getStatus()
        );

        verify(orderRepository).findById(id);

        verify(orderRepository, never())
                .save(any(Order.class));

        verifyNoInteractions(orderMapper);
    }


    @Test
    void shouldThrowResourceNotFoundWhenChangingStatusOfMissingOrder() {
        ChangeOrderStatusRequest request =
                new ChangeOrderStatusRequest(
                        OrderStatus.IN_TRANSIT
                );

        when(orderRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> orderService.changeStatus(id, request)
        )
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage(
                        "Orden no encontrada con id: " + id
                );

        verify(orderRepository).findById(id);

        verify(orderRepository, never())
                .save(any(Order.class));

        verifyNoInteractions(orderMapper);
    }

    @Test
    void shouldListOrdersUsingFilters() {
        Instant from =
                Instant.parse("2026-10-01T00:00:00Z");

        Instant to =
                Instant.parse("2026-10-31T23:59:59Z");

        OrderFilterRequest filters =
                new OrderFilterRequest(
                        OrderStatus.CREATED,
                        "Guadalajara",
                        null,
                        from,
                        to
                );

        when(orderRepository.findAll(
                any(Specification.class)))
                .thenReturn(List.of(order));

        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        List<OrderResponse> result =
                orderService.listOrdersByFilter(filters);

        assertEquals(1, result.size());
        assertEquals(orderResponse, result.get(0));

        verify(orderRepository)
                .findAll(any(Specification.class));

        verify(orderMapper)
                .toResponse(order);
    }


    @Test
    void shouldReturnEmptyListWhenNoOrdersMatchFilters() {
        OrderFilterRequest filters =
                new OrderFilterRequest(
                        OrderStatus.DELIVERED,
                        null,
                        null,
                        null,
                        null
                );

        when(orderRepository.findAll(
                any(Specification.class)
        )).thenReturn(List.of());

        List<OrderResponse> result =
                orderService.listOrdersByFilter(filters);

        assertTrue(result.isEmpty());

        verify(orderRepository)
                .findAll(any(Specification.class));

        verifyNoInteractions(orderMapper);
    }


    @Test
    void shouldRejectInvalidDateRange() {
        Instant from =
                Instant.parse("2026-12-01T00:00:00Z");

        Instant to =
                Instant.parse("2026-10-01T00:00:00Z");

        OrderFilterRequest filters =
                new OrderFilterRequest(
                        null,
                        null,
                        null,
                        from,
                        to
                );

        assertThatThrownBy(
                () -> orderService.listOrdersByFilter(filters)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "La fecha inicial no puede ser posterior a la fecha final"
                );

        verify(
                orderRepository,
                never()
        ).findAll(any(Specification.class));

        verifyNoInteractions(orderMapper);
    }
}