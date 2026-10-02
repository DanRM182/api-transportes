package com.transport.order_service.client;

import com.transport.order_service.common.exception.ExternalServiceException;
import com.transport.order_service.common.exception.ResourceNotFoundException;
import com.transport.order_service.dto.response.DriverClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DriverClient {
    private final RestClient driverRestClient;

    public DriverClientResponse findActiveDriver(UUID driverId) {
        try {
            return driverRestClient.get()
                    .uri("/api/drivers/{id}", driverId)
                    .retrieve()
                    .body(DriverClientResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Conductor activo no encontrado con id: " + driverId);
        } catch (RestClientException e) {
            throw new ExternalServiceException("No fue posible consultar el servicio de conductores");
        }
    }
}
