package com.transport.order_service.common.response;

public record CustomErrorResponse(
        int code,
        String message
) { }