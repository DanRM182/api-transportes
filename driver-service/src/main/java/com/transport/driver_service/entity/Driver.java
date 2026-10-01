package com.transport.driver_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity @Getter
@Table(name = "drivers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "license_number", nullable = false, length = 30, unique = true)
    private String licenseNumber;

    @Column(nullable = false)
    private boolean active;

    private static String validateString(String data, String message) {
        if(data == null || data.isBlank())
            throw new IllegalArgumentException(message);

        return data.trim();
    }

    private Driver(String name, String licenseNumber) {
        this.name = validateString(name,
                "El nombre del conductor es requerido");
        this.licenseNumber = validateString(licenseNumber,
                "El número de licencia es requerido");
    }

    public static Driver create(String name, String licenseNumber) {
        return new Driver(name, licenseNumber);
    }
}
