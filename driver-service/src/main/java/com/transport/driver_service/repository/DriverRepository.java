package com.transport.driver_service.repository;

import com.transport.driver_service.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
    List<Driver> findByActiveTrue();

    Optional<Driver> findByIdAndActiveTrue(UUID id);

    boolean existsByLicenseNumber(String licenseNumber);
}
