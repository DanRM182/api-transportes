package com.transport.order_service.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Entity @Getter
@Table(name = "order_assignments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Column(name = "pdf_path", length = 255)
    private String pdfPath;

    @Column(name = "image_path", length = 255)
    private String imagePath;

    private static String validateString(String data, String message) {
        if(data == null || data.isBlank())
            throw new IllegalArgumentException(message);

        return data.trim();
    }

    private OrderAssignment(Order order, UUID driverId) {
        Objects.requireNonNull(order, "La orden es requerida");

        Objects.requireNonNull(driverId, "El conductor es requerido");

        this.order = order;
        this.driverId = driverId;
    }

    public static OrderAssignment create(Order order, UUID driverId) {return new OrderAssignment(order, driverId);}

    public void addPdfFile(String pdfPath) {
        this.pdfPath = validateString(pdfPath,
                "La dirección del PDF es requerida");
    }

    public void addImage(String imagePath) {
        this.imagePath = validateString(imagePath,
                "La dirección de la imagen es requerida");
    }
}
