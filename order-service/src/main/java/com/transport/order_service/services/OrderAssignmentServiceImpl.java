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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class OrderAssignmentServiceImpl implements OrderAssignmentService {
    private final OrderAssignmentRepository orderAssignmentRepository;
    private final OrderAssignmentMapper orderAssignmentMapper;
    private final OrderRepository orderRepository;
    private final DriverClient driverClient;
    private final FileStorageService fileStorageService;

    @Override
    public OrderAssignmentResponse assignDriver(UUID orderId, AssignDriverRequest request) {
        log.info("Asignando conductor {} a orden {}",
                request.driverId(),
                orderId);

        Order order = findOrderById(orderId);

        validateOrderStatus(order);

        validateOrderIdInExistentOrderAssignments(orderId);

        driverClient.findActiveDriver(request.driverId());

        OrderAssignment assignment = orderAssignmentMapper.toEntity(
                order,
                request.driverId());

        OrderAssignment savedAssignment = orderAssignmentRepository.save(assignment);

        log.info("Conductor {} asignado correctamente a orden {}",
                request.driverId(),
                orderId);

        return orderAssignmentMapper.toResponse(savedAssignment);
    }

    @Override
    public OrderAssignmentResponse addPdf(UUID orderId, MultipartFile file) {
        OrderAssignment assignment = findAssignmentByOrderId(orderId);

        String path = fileStorageService.storePdf(file);

        assignment.addPdfFile(path);

        OrderAssignment saved = orderAssignmentRepository.save(assignment);

        log.info("PDF agregado a la asignación de la orden {}", orderId);

        return  orderAssignmentMapper.toResponse(saved);
    }

    @Override
    public OrderAssignmentResponse addImage(UUID orderId, MultipartFile file) {
        OrderAssignment assignment = findAssignmentByOrderId(orderId);

        String path = fileStorageService.storePdf(file);

        assignment.addImage(path);

        OrderAssignment saved = orderAssignmentRepository.save(assignment);

        log.info("Imagen agregada a la asignación de la orden {}", orderId);

        return  orderAssignmentMapper.toResponse(saved);
    }

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Orden no encontrada con id: " + orderId));
    }

    private void validateOrderStatus(Order order) {
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new OrderAlreadyAssignedException(
                    "Solo se puede asignar un conductor a una orden en estado CREATED"
            );
        }
    }

    private void validateOrderIdInExistentOrderAssignments(UUID orderId) {
        if(orderAssignmentRepository.existsByOrderId(orderId))
            throw new IllegalArgumentException("Ya hay un conductor asignado a la orden");
    }

    private OrderAssignment findAssignmentByOrderId(UUID orderId) {
        return orderAssignmentRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe una asignación para la orden " + orderId));
    }
}
