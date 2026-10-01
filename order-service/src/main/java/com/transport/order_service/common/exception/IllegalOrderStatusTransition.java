package com.transport.order_service.common.exception;

public class IllegalOrderStatusTransition extends RuntimeException {
    public IllegalOrderStatusTransition(String message) {super(message);}
}
