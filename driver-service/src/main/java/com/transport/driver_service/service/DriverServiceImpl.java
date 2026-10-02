package com.transport.driver_service.service;

import com.transport.driver_service.common.exception.DuplicateResourceException;
import com.transport.driver_service.common.exception.ResourceNotFoundException;
import com.transport.driver_service.dto.request.CreateDriverRequest;
import com.transport.driver_service.dto.response.DriverResponse;
import com.transport.driver_service.entity.Driver;
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
        validateUniqueLicenseNumber(request.licenseNumber());

        log.info("Registrando nuevo conductor: {}", request.name());

        Driver driver = driverMapper.toEntity(request);

        Driver savedDriver = driverRepository.save(driver);

        log.info("Nuevo conductor registrado con id: {}", savedDriver.getId());

        return driverMapper.toResponse(savedDriver);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> listActiveDrivers() {
        log.info("Listando todos los conductores activos");

        return driverRepository.findByActiveTrue().stream()
                .map(driverMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponse findActiveDriver(UUID id) {
        log.info("Buscar conductor activo por id {}", id);

        return driverMapper.toResponse(findActiveDriverById(id));
    }

    private void validateUniqueLicenseNumber(String licenseNumber) {
        if(driverRepository.existsByLicenseNumber(licenseNumber))
            throw new DuplicateResourceException(
                    "La licencia de conducir está duplicada");
    }

    private Driver findActiveDriverById(UUID id) {
        return driverRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conductor activo no encontrado con id: " + id));
    }
}