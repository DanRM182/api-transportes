package com.transport.driver_service.controller;

import com.transport.driver_service.dto.request.CreateDriverRequest;
import com.transport.driver_service.dto.response.DriverResponse;
import com.transport.driver_service.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Drivers", description = "Métodos para gestión de conductores")
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    @Operation(summary = "Listar conductores activos")
    public ResponseEntity<List<DriverResponse>> listActiveDrivers() {
        return ResponseEntity.ok(driverService.listActiveDrivers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener conductor activo por ID")
    public ResponseEntity<DriverResponse> findActiveDriverById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(driverService.findActiveDriver(id));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo conductor")
    public ResponseEntity<DriverResponse> create(
            @Valid @RequestBody CreateDriverRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(driverService.create(request));
    }
}
