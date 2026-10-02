package com.transport.order_service.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class DriverClient {
    @Bean
    public RestClient driverRestClient() {
        return RestClient.builder()
                .baseUrl("/api/drivers/{idConductor}")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
