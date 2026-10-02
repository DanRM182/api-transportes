package com.transport.order_service.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.order_service.dto.request.AssignDriverRequest;
import com.transport.order_service.dto.response.OrderAssignmentResponse;
import com.transport.order_service.security.JwtService;
import com.transport.order_service.services.OrderAssignmentService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderAssignmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderAssignmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private OrderAssignmentService orderAssignmentService;

    @Test
    void shouldAssignDriverSuccessfully() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        AssignDriverRequest request =
                new AssignDriverRequest(driverId);

        OrderAssignmentResponse response =
                new OrderAssignmentResponse(
                        UUID.randomUUID(),
                        orderId,
                        driverId,
                        null,
                        null);

        when(orderAssignmentService.assignDriver(
                eq(orderId),
                any(AssignDriverRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/orders/{orderId}/assignments", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId")
                        .value(orderId.toString()))
                .andExpect(jsonPath("$.driverId")
                        .value(driverId.toString()));
    }

    @Test
    void shouldAddPdfSuccessfully() throws Exception {
        UUID orderId = UUID.randomUUID();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "document.pdf",
                        "application/pdf",
                        "PDF".getBytes());

        OrderAssignmentResponse response =
                new OrderAssignmentResponse(
                        UUID.randomUUID(),
                        orderId,
                        UUID.randomUUID(),
                        "document.pdf",
                        null);

        when(orderAssignmentService.addPdf(
                eq(orderId),
                any(MultipartFile.class)))
                .thenReturn(response);

        mockMvc.perform(multipart(
                        "/api/orders/{orderId}/assignments/pdf",
                        orderId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;}))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pdfPath")
                        .value("document.pdf"));
    }

    @Test
    void shouldAddImageSuccessfully() throws Exception {
        UUID orderId = UUID.randomUUID();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "image.jpg",
                        "image/jpeg",
                        "IMAGE CONTENT".getBytes());

        OrderAssignmentResponse response =
                new OrderAssignmentResponse(
                        UUID.randomUUID(),
                        orderId,
                        UUID.randomUUID(),
                        null,
                        "image.jpg");

        when(orderAssignmentService.addImage(
                eq(orderId),
                any(MultipartFile.class)))
                .thenReturn(response);

        mockMvc.perform(multipart(
                        "/api/orders/{orderId}/assignments/image",
                        orderId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;}))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagePath")
                        .value("image.jpg"));
    }
}