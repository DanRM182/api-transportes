package com.transport.order_service.client;

import com.transport.order_service.dto.response.DriverClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DriverClient {
    private final RestClient driverRestClient;

    public DriverClientResponse findActiveDriver(UUID driverId) {

        return driverRestClient.get()
                .uri("/api/drivers/{id}", driverId)
                .retrieve()
                .body(DriverClientResponse.class);
    }
}
