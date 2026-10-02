package com.transport.order_service.services;

import com.transport.order_service.dto.request.AssignDriverRequest;
import com.transport.order_service.dto.response.OrderAssignmentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface OrderAssignmentService {
    OrderAssignmentResponse assignDriver(UUID orderId, AssignDriverRequest request);

    OrderAssignmentResponse addPdf(UUID orderId, MultipartFile file);

    OrderAssignmentResponse addImage(UUID orderId, MultipartFile file);
}
