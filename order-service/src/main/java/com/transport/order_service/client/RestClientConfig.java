package com.transport.order_service.client;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient driverRestClient(
            RestClient.Builder builder,
            @Value("${services.driver.url}") String driverServiceUrl) {

        return builder
                .baseUrl(driverServiceUrl)
                .requestInterceptor((request, body, execution) -> {
                    var attributes = RequestContextHolder.getRequestAttributes();

                    if (attributes instanceof ServletRequestAttributes servletAttributes) {
                        HttpServletRequest currentRequest =
                                servletAttributes.getRequest();

                        String authorization =
                                currentRequest.getHeader("Authorization");

                        if (authorization != null &&
                                authorization.startsWith("Bearer ")) {
                            request.getHeaders()
                                    .set("Authorization", authorization);
                        }
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}
