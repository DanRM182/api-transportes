package com.transport.order_service.controller;

import com.transport.order_service.dto.request.ChangeOrderStatusRequest;
import com.transport.order_service.dto.request.CreateOrderRequest;
import com.transport.order_service.dto.request.OrderFilterRequest;
import com.transport.order_service.dto.response.OrderResponse;
import com.transport.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Métodos para gestión de órdenes")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Listar ordenes por filtros indicados")
    public ResponseEntity<List<OrderResponse>> listFilteredOrders(@ModelAttribute OrderFilterRequest filters) {
        return ResponseEntity.ok(orderService.listOrdersByFilter(filters));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden por ID")
    public ResponseEntity<OrderResponse> findOrderById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(orderService.findOrder(id));
    }

    @PostMapping
    @Operation(summary = "Registrar orden")
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(request));
    }

    @PatchMapping("{id}/status")
    @Operation(summary = "Actualizar estado de una orden")
    public ResponseEntity<OrderResponse> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeOrderStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.changeStatus(id, request));
    }
}
