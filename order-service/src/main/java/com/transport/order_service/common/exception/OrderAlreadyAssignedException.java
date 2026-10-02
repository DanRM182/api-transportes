package com.transport.order_service.common.exception;

public class OrderAlreadyAssignedException extends RuntimeException {
    public OrderAlreadyAssignedException(String message) {
        super(message);
    }
}
