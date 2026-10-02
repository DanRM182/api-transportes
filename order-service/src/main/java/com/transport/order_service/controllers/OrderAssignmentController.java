package com.transport.order_service.controllers;

import com.transport.order_service.dto.request.AssignDriverRequest;
import com.transport.order_service.dto.response.OrderAssignmentResponse;
import com.transport.order_service.services.OrderAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders/{orderId}/assignments")
@Tag(name = "Order Assignments", description = "Gestión de asignaciones de conductores a órdenes")
public class OrderAssignmentController {
    private final OrderAssignmentService orderAssignmentService;

    @PostMapping
    @Operation(summary = "Asignar conductor a una orden")
    public ResponseEntity<OrderAssignmentResponse> assignDriver(
            @PathVariable UUID orderId,
            @Valid @RequestBody AssignDriverRequest request) {

        OrderAssignmentResponse response =
                orderAssignmentService.assignDriver(orderId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
