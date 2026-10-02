package com.transport.order_service.services;

import com.transport.order_service.dto.request.AssignDriverRequest;
import com.transport.order_service.dto.response.OrderAssignmentResponse;

import java.util.UUID;

public interface OrderAssignmentService {
    OrderAssignmentResponse assignDriver(UUID orderId, AssignDriverRequest request);
}
