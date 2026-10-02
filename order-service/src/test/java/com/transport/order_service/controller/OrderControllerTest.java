package com.transport.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.order_service.common.exception.IllegalOrderStatusTransition;
import com.transport.order_service.common.exception.ResourceNotFoundException;
import com.transport.order_service.dto.request.ChangeOrderStatusRequest;
import com.transport.order_service.dto.request.CreateOrderRequest;
import com.transport.order_service.dto.request.OrderFilterRequest;
import com.transport.order_service.dto.response.OrderResponse;
import com.transport.order_service.enums.OrderStatus;
import com.transport.order_service.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private UUID id;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {

        id = UUID.randomUUID();

        orderResponse = new OrderResponse(
                id,
                OrderStatus.CREATED,
                "Guadalajara",
                "Ciudad de México",
                Instant.parse("2026-10-01T20:30:00Z"),
                Instant.parse("2026-10-01T20:30:00Z")
        );
    }

    @Test
    void shouldReturnOrdersListAnd200() throws Exception {
        when(orderService.listOrdersByFilter(
                any(OrderFilterRequest.class)
        )).thenReturn(List.of(orderResponse));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(id.toString()))
                .andExpect(jsonPath("$[0].status")
                        .value("CREATED"))
                .andExpect(jsonPath("$[0].origin")
                        .value("Guadalajara"))
                .andExpect(jsonPath("$[0].destination")
                        .value("Ciudad de México"));

        verify(orderService)
                .listOrdersByFilter(any(OrderFilterRequest.class));
    }

    @Test
    void shouldReturnEmptyListAnd200() throws Exception {
        when(orderService.listOrdersByFilter(
                any(OrderFilterRequest.class)
        )).thenReturn(List.of());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(orderService)
                .listOrdersByFilter(any(OrderFilterRequest.class));
    }

    @Test
    void shouldBindFiltersCorrectly() throws Exception {
        when(orderService.listOrdersByFilter(
                any(OrderFilterRequest.class)
        )).thenReturn(List.of());

        mockMvc.perform(get("/api/orders")
                        .param("status", "IN_TRANSIT")
                        .param("origin", "Guadalajara")
                        .param("destination", "Monterrey")
                        .param("createdFrom", "2026-10-01T00:00:00Z")
                        .param("createdTo", "2026-10-31T23:59:59Z"))
                .andExpect(status().isOk());

        ArgumentCaptor<OrderFilterRequest> captor =
                ArgumentCaptor.forClass(OrderFilterRequest.class);

        verify(orderService)
                .listOrdersByFilter(captor.capture());

        OrderFilterRequest filters =
                captor.getValue();

        assertEquals(OrderStatus.IN_TRANSIT,
                filters.status()
        );

        assertEquals(
                "Guadalajara",
                filters.origin());

        assertEquals(
                "Monterrey",
                filters.destination());

        assertEquals(
                Instant.parse("2026-10-01T00:00:00Z"),
                filters.createdFrom());

        assertEquals(
                Instant.parse("2026-10-31T23:59:59Z"),
                filters.createdTo());
    }

    @Test
    void shouldReturnOrderWhenExists() throws Exception {
        when(orderService.findOrder(id))
                .thenReturn(orderResponse);

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(id.toString()))
                .andExpect(jsonPath("$.status")
                        .value("CREATED"))
                .andExpect(jsonPath("$.origin")
                        .value("Guadalajara"))
                .andExpect(jsonPath("$.destination")
                        .value("Ciudad de México"))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-10-01T20:30:00Z"))
                .andExpect(jsonPath("$.updatedAt")
                        .value("2026-10-01T20:30:00Z"));

        verify(orderService).findOrder(id);
    }


    @Test
    void shouldReturn404WhenOrderDoesNotExist() throws Exception {
        String message =
                "Orden no encontrada con id: " + id;

        when(orderService.findOrder(id))
                .thenThrow(new ResourceNotFoundException(message));

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value(message));

        verify(orderService).findOrder(id);
    }

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        CreateOrderRequest request =
                new CreateOrderRequest(
                        "Guadalajara",
                        "Ciudad de México");

        when(orderService.create(any(CreateOrderRequest.class)))
                .thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(id.toString()))
                .andExpect(jsonPath("$.status")
                        .value("CREATED"))
                .andExpect(jsonPath("$.origin")
                        .value("Guadalajara"))
                .andExpect(jsonPath("$.destination")
                        .value("Ciudad de México"));

        verify(orderService)
                .create(any(CreateOrderRequest.class));
    }

    @Test
    void shouldReturn400WhenOriginIsBlank() throws Exception {
        CreateOrderRequest request =
                new CreateOrderRequest(
                        "",
                        "Ciudad de México");

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("origin: El origen es requerido"));

        verifyNoInteractions(orderService);
    }

    @Test
    void shouldReturn400WhenDestinationIsBlank() throws Exception {
        CreateOrderRequest request =
                new CreateOrderRequest(
                        "Guadalajara",
                        "");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("destination: El destino es requerido"));

        verifyNoInteractions(orderService);
    }

    @Test
    void shouldChangeStatusSuccessfully() throws Exception {
        ChangeOrderStatusRequest request =
                new ChangeOrderStatusRequest(
                        OrderStatus.IN_TRANSIT);

        OrderResponse updatedResponse =
                new OrderResponse(
                        id,
                        OrderStatus.IN_TRANSIT,
                        "Guadalajara",
                        "Ciudad de México",
                        Instant.parse(
                            "2026-10-01T20:30:00Z"),
                        Instant.parse(
                                "2026-10-02T10:00:00Z")
                );

        when(orderService.changeStatus(
                eq(id),
                any(ChangeOrderStatusRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/orders/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(id.toString()))
                .andExpect(jsonPath("$.status")
                        .value("IN_TRANSIT"))
                .andExpect(jsonPath("$.origin")
                        .value("Guadalajara"));

        verify(orderService)
                .changeStatus(
                        eq(id),
                        any(ChangeOrderStatusRequest.class));
    }


    @Test
    void shouldReturn409WhenTransitionIsInvalid() throws Exception {
        ChangeOrderStatusRequest request =
                new ChangeOrderStatusRequest(
                        OrderStatus.DELIVERED);

        String message =
                "No se puede pasar del status CREATED a DELIVERED";

        when(orderService.changeStatus(
                eq(id),
                any(ChangeOrderStatusRequest.class)))
                .thenThrow(
                new IllegalOrderStatusTransition(message));

        mockMvc.perform(patch("/api/orders/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code")
                        .value(409))
                .andExpect(jsonPath("$.message")
                        .value(message));

        verify(orderService)
                .changeStatus(
                        eq(id),
                        any(ChangeOrderStatusRequest.class));
    }

    @Test
    void shouldReturn400WhenStatusIsNull() throws Exception {
        ChangeOrderStatusRequest request =
                new ChangeOrderStatusRequest(null);


        mockMvc.perform(patch("/api/orders/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("status: El estado de la orden es requerido"));

        verifyNoInteractions(orderService);
    }

    @Test
    void shouldReturn404WhenChangingStatusOfMissingOrder() throws Exception {
        ChangeOrderStatusRequest request =
                new ChangeOrderStatusRequest(
                        OrderStatus.IN_TRANSIT);

        String message =
                "Orden no encontrada con id: " + id;

        when(orderService.changeStatus(
                eq(id),
                any(ChangeOrderStatusRequest.class)))
                .thenThrow(
                new ResourceNotFoundException(message));


        mockMvc.perform(patch("/api/orders/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value(message));

        verify(orderService)
                .changeStatus(
                        eq(id),
                        any(ChangeOrderStatusRequest.class));
    }
}