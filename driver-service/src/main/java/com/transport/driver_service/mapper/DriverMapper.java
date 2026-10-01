package com.transport.driver_service.mapper;

import com.transport.driver_service.dto.request.CreateDriverRequest;
import com.transport.driver_service.dto.response.DriverResponse;
import com.transport.driver_service.entity.Driver;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    default Driver toEntity(CreateDriverRequest request) {
        return Driver.create(
                request.name(),
                request.licenseNumber()
        );
    }

    DriverResponse toResponse(Driver driver);
}
