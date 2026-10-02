package com.transport.driver_service.common.response;

public record CustomErrorResponse(
        int code,
        String message
) { }