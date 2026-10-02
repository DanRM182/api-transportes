package com.transport.driver_service.service;

import com.transport.driver_service.dto.request.CreateDriverRequest;
import com.transport.driver_service.dto.response.DriverResponse;

import java.util.List;
import java.util.UUID;

public interface DriverService {
    DriverResponse create(CreateDriverRequest request);

    List<DriverResponse> listActiveDrivers();

    DriverResponse findActiveDriver(UUID id);
}
