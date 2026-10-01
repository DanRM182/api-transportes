package com.transport.driver_service.service;

import com.transport.driver_service.dto.request.CreateDriverRequest;
import com.transport.driver_service.dto.response.DriverResponse;
import com.transport.driver_service.mapper.DriverMapper;
import com.transport.driver_service.repository.DriverRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    public DriverResponse create(CreateDriverRequest request) {
        log.info("Registrando nuevo conductor: {}", request.name());

        return null;
    }

    @Override
    public List<DriverResponse> listActiveDrivers() {
        return List.of();
    }

    @Override
    public DriverResponse findActiveDriver(UUID id) {
        return null;
    }

    private void validateUniqueLicenseNumber(String licenseNumber) {
        if(driverRepository.existsByLicenseNumber(licenseNumber))
            throw new
    }
}