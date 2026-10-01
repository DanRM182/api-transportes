package com.transport.driver_service.dto;

public record CustomErrorResponse(
        int code,
        String message
) { }