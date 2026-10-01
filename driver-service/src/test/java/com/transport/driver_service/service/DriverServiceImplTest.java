package com.transport.driver_service.service;

import com.transport.driver_service.common.exception.DuplicateResourceException;
import com.transport.driver_service.common.exception.ResourceNotFoundException;
import com.transport.driver_service.dto.request.CreateDriverRequest;
import com.transport.driver_service.dto.response.DriverResponse;
import com.transport.driver_service.entity.Driver;
import com.transport.driver_service.mapper.DriverMapper;
import com.transport.driver_service.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {
    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverMapper driverMapper;

    @InjectMocks
    private DriverServiceImpl driverService;

    private CreateDriverRequest request;

    private Driver driver;

    private DriverResponse driverResponse;

    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        request = new CreateDriverRequest(
                "Juan Pérez Martínez",
                "RAF324EF");

        driver = Driver.create(
                "Juan Pérez Martínez",
                "RAF324EF");

        driverResponse = new DriverResponse(
                id,
                "Juan Pérez Martínez",
                "RAF324EF",
                true);
    }

    @Test
    void shouldCreateDriverWhenLicenseNumberIsUnique() {
        when(driverRepository.existsByLicenseNumber(request.licenseNumber()))
                .thenReturn(false);

        when(driverMapper.toEntity(request))
                .thenReturn(driver);

        when(driverRepository.save(driver))
                .thenReturn(driver);

        when(driverMapper.toResponse(driver))
                .thenReturn(driverResponse);

        DriverResponse result = driverService.create(request);

        assertThat(result).isEqualTo(driverResponse);

        verify(driverRepository)
                .existsByLicenseNumber(request.licenseNumber());

        verify(driverRepository)
                .save(driver);
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenLicenseNumberAlreadyExists() {
        when(driverRepository.existsByLicenseNumber(request.licenseNumber()))
                .thenReturn(true);

        assertThatThrownBy(() -> driverService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(
                        "La licencia de conducir está duplicada");

        verify(driverRepository)
                .existsByLicenseNumber(request.licenseNumber());

        verify(driverMapper, never())
                .toEntity(any(CreateDriverRequest.class));

        verify(driverRepository, never())
                .save(any(Driver.class));
    }

    @Test
    void shouldFindActiveDriverWhenDriverExists() {
        when(driverRepository.findByIdAndActiveTrue(id))
                .thenReturn(Optional.of(driver));

        when(driverMapper.toResponse(driver))
                .thenReturn(driverResponse);

        DriverResponse result = driverService.findActiveDriver(id);

        assertThat(result).isEqualTo(driverResponse);

        verify(driverRepository)
                .findByIdAndActiveTrue(id);

        verify(driverMapper)
                .toResponse(driver);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenFindActiveDriverDoesNotExist() {
        when(driverRepository.findByIdAndActiveTrue(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.findActiveDriver(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(
                        "Conductor activo no encontrado con id: " + id);

        verify(driverRepository)
                .findByIdAndActiveTrue(id);

        verify(driverMapper, never())
                .toResponse(any(Driver.class));
    }

    @Test
    void shouldListActiveDriversWhenDriversExist() {
        when(driverRepository.findByActiveTrue())
                .thenReturn(List.of(driver));

        when(driverMapper.toResponse(driver))
                .thenReturn(driverResponse);

        List<DriverResponse> driversList =
                driverService.listActiveDrivers();

        assertThat(driversList)
                .containsExactly(driverResponse);

        verify(driverRepository).findByActiveTrue();
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveDriversExist() {
        when(driverRepository.findByActiveTrue())
                .thenReturn(List.of());

        List<DriverResponse> driversList =
                driverService.listActiveDrivers();

        assertThat(driversList).isEmpty();

        verify(driverRepository).findByActiveTrue();

        verify(driverMapper, never())
                .toResponse(any(Driver.class));
    }
}
