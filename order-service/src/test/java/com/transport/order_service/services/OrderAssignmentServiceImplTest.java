package com.transport.order_service.services;

import com.transport.order_service.client.DriverClient;
import com.transport.order_service.common.exception.OrderAlreadyAssignedException;
import com.transport.order_service.common.exception.ResourceNotFoundException;
import com.transport.order_service.dto.request.AssignDriverRequest;
import com.transport.order_service.dto.response.OrderAssignmentResponse;
import com.transport.order_service.entities.Order;
import com.transport.order_service.entities.OrderAssignment;
import com.transport.order_service.enums.OrderStatus;
import com.transport.order_service.mappers.OrderAssignmentMapper;
import com.transport.order_service.repositories.OrderAssignmentRepository;
import com.transport.order_service.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderAssignmentServiceImplTest {
    @Mock
    private OrderAssignmentRepository orderAssignmentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderAssignmentMapper orderAssignmentMapper;

    @Mock
    private DriverClient driverClient;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private OrderAssignmentServiceImpl orderAssignmentService;

    UUID orderId;
    UUID driverId;
    Order order;
    AssignDriverRequest request;
    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        driverId = UUID.randomUUID();

        order = Order.create(
                "Guadalajara",
                "Monterrey");

        request = new AssignDriverRequest(driverId);
    }

    @Test
    void shouldAssignDriverSuccessfully() {
        UUID assignmentId = UUID.randomUUID();

        OrderAssignment assignment =
                OrderAssignment.create(order, driverId);

        OrderAssignmentResponse expectedResponse =
                new OrderAssignmentResponse(
                        assignmentId,
                        orderId,
                        driverId,
                        null,
                        null);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderAssignmentRepository.existsByOrderId(orderId))
                .thenReturn(false);

        when(orderAssignmentMapper.toEntity(order, driverId))
                .thenReturn(assignment);

        when(orderAssignmentRepository.save(assignment))
                .thenReturn(assignment);

        when(orderAssignmentMapper.toResponse(assignment))
                .thenReturn(expectedResponse);

        OrderAssignmentResponse response =
                orderAssignmentService.assignDriver(orderId, request);

        assertNotNull(response);
        assertEquals(driverId, response.driverId());

        verify(driverClient).findActiveDriver(driverId);
        verify(orderAssignmentRepository).save(assignment);
    }

    @Test
    void shouldRejectAssignmentWhenOrderDoesNotExist() {
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderAssignmentService.assignDriver(
                        orderId,
                        request));

        verifyNoInteractions(driverClient);
        verify(orderAssignmentRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectAssignmentWhenOrderIsNotCreated() {
        order.updateOrderStatus(OrderStatus.IN_TRANSIT);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                OrderAlreadyAssignedException.class,
                () -> orderAssignmentService.assignDriver(
                        orderId,
                        request));

        verifyNoInteractions(driverClient);

        verify(orderAssignmentRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectAssignmentWhenOrderAlreadyAssigned() {
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderAssignmentRepository.existsByOrderId(orderId))
                .thenReturn(true);

        assertThrows(
                OrderAlreadyAssignedException.class,
                () -> orderAssignmentService.assignDriver(
                        orderId,
                        request));

        verifyNoInteractions(driverClient);

        verify(orderAssignmentRepository, never())
                .save(any());
    }

    @Test
    void shouldAddPdfSuccessfully() {
        Order order = Order.create(
                "Guadalajara",
                "Monterrey");

        OrderAssignment assignment =
                OrderAssignment.create(order, driverId);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "document.pdf",
                        "application/pdf",
                        "PDF CONTENT".getBytes());

        OrderAssignmentResponse expectedResponse =
                new OrderAssignmentResponse(
                        UUID.randomUUID(),
                        orderId,
                        driverId,
                        "document.pdf",
                        null);

        when(orderAssignmentRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(assignment));

        when(fileStorageService.storePdf(file))
                .thenReturn("document.pdf");

        when(orderAssignmentRepository.save(assignment))
                .thenReturn(assignment);

        when(orderAssignmentMapper.toResponse(assignment))
                .thenReturn(expectedResponse);

        OrderAssignmentResponse response =
                orderAssignmentService.addPdf(orderId, file);

        assertNotNull(response);
        assertEquals("document.pdf", assignment.getPdfPath());

        verify(fileStorageService).storePdf(file);
        verify(orderAssignmentRepository).save(assignment);
    }

    @Test
    void shouldAddImageSuccessfully() {

        OrderAssignment assignment =
                OrderAssignment.create(order, driverId);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "image.jpg",
                        "image/jpeg",
                        "IMAGE CONTENT".getBytes());

        OrderAssignmentResponse expectedResponse =
                new OrderAssignmentResponse(
                        UUID.randomUUID(),
                        orderId,
                        driverId,
                        null,
                        "image.jpg");

        when(orderAssignmentRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(assignment));

        when(fileStorageService.storeImage(file))
                .thenReturn("image.jpg");

        when(orderAssignmentRepository.save(assignment))
                .thenReturn(assignment);

        when(orderAssignmentMapper.toResponse(assignment))
                .thenReturn(expectedResponse);

        OrderAssignmentResponse response =
                orderAssignmentService.addImage(orderId, file);

        assertNotNull(response);
        assertEquals("image.jpg", assignment.getImagePath());

        verify(fileStorageService).storeImage(file);
        verify(orderAssignmentRepository).save(assignment);
    }

    @Test
    void shouldRejectFileWhenAssignmentDoesNotExist() {
        UUID orderId = UUID.randomUUID();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "document.pdf",
                        "application/pdf",
                        "PDF".getBytes());

        when(orderAssignmentRepository.findByOrderId(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderAssignmentService.addPdf(orderId, file));

        verifyNoInteractions(fileStorageService);
    }

}
