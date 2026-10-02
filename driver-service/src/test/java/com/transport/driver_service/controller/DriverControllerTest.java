package com.transport.driver_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.driver_service.common.exception.GlobalExceptionHandler;
import com.transport.driver_service.common.exception.ResourceNotFoundException;
import com.transport.driver_service.dto.request.CreateDriverRequest;
import com.transport.driver_service.dto.response.DriverResponse;
import com.transport.driver_service.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DriverControllerTest {
    @Mock
    private DriverService driverService;

    @InjectMocks
    private DriverController driverController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private CreateDriverRequest request;

    private DriverResponse response;

    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        mockMvc = MockMvcBuilders
                .standaloneSetup(driverController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();


        request = new CreateDriverRequest(
                "Juan Pérez Martínez",
                "RAF324EF");

        response = new DriverResponse(
                id,
                "Juan Pérez Martínez",
                "RAF324EF",
                true);
    }

    @Test
    void shouldCreateDriverAndReturn201() throws Exception {
        when(driverService.create(request))
                .thenReturn(response);

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(id.toString()))
                .andExpect(jsonPath("$.name")
                        .value("Juan Pérez Martínez"))
                .andExpect(jsonPath("$.licenseNumber")
                        .value("RAF324EF"))
                .andExpect(jsonPath("$.active")
                        .value(true));

        verify(driverService).create(request);
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        CreateDriverRequest invalidRequest =
                new CreateDriverRequest(
                        "",
                        "RAF324EF"
                );

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message")
                        .value("name: El nombre es requerido"));

        verifyNoInteractions(driverService);
    }

    @Test
    void shouldReturnDriversListAnd200() throws Exception {
        when(driverService.listActiveDrivers())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()")
                        .value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(id.toString()))
                .andExpect(jsonPath("$[0].name")
                        .value("Juan Pérez Martínez"))
                .andExpect(jsonPath("$[0].licenseNumber")
                        .value("RAF324EF"))
                .andExpect(jsonPath("$[0].active")
                        .value(true));

        verify(driverService).listActiveDrivers();
    }

    @Test
    void shouldReturnEmptyListAnd200() throws Exception {
        when(driverService.listActiveDrivers())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()")
                        .value(0));

        verify(driverService).listActiveDrivers();
    }

    @Test
    void shouldReturnDriverAnd200() throws Exception {
        when(driverService.findActiveDriver(id))
                .thenReturn(response);

        mockMvc.perform(get("/api/drivers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(id.toString()))
                .andExpect(jsonPath("$.name")
                        .value("Juan Pérez Martínez"))
                .andExpect(jsonPath("$.licenseNumber")
                        .value("RAF324EF"))
                .andExpect(jsonPath("$.active")
                        .value(true));

        verify(driverService).findActiveDriver(id);
    }

    @Test
    void shouldReturnNullAnd404() throws Exception {
        when(driverService.findActiveDriver(id))
                .thenThrow(new ResourceNotFoundException(
                        "Conductor activo no encontrado con id: " + id
                ));

        mockMvc.perform(get("/api/drivers/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Conductor activo no encontrado con id: " + id));

        verify(driverService).findActiveDriver(id);
    }
}
