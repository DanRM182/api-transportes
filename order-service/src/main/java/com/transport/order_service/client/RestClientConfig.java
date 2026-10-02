package com.transport.order_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient driverRestClient(
            RestClient.Builder builder,
            @Value("${services.driver.url}") String driverServiceUrl) {

        return builder
                .baseUrl(driverServiceUrl)
                .build();
    }
}
