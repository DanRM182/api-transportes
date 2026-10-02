package com.transport.order_service.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;
@Service
public class LocalFileStorageService implements FileStorageService {

    private static final Set<String> IMAGE_TYPES =
            Set.of("image/png", "image/jpeg");

    private final Path storageLocation;

    public LocalFileStorageService(@Value("${storage.location:uploads}") String storageLocation) {
        this.storageLocation = Path.of(storageLocation)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No fue posible crear el directorio de archivos", e);
        }
    }

    @Override
    public String storePdf(MultipartFile file) {
        validateFile(file);

        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException(
                    "El archivo debe ser de tipo PDF");
        }

        return store(file, ".pdf");
    }

    @Override
    public String storeImage(MultipartFile file) {
        validateFile(file);

        if (!IMAGE_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    "La imagen debe ser PNG o JPG");
        }

        String extension =
                "image/png".equals(file.getContentType())
                        ? ".png"
                        : ".jpg";

        return store(file, extension);
    }

    private String store(MultipartFile file, String extension) {

        String filename = UUID.randomUUID() + extension;

        Path destination = storageLocation
                .resolve(filename)
                .normalize();

        if (!destination.startsWith(storageLocation)) {
            throw new IllegalArgumentException(
                    "Ruta de almacenamiento inválida");
        }

        try {
            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);

            return filename;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No fue posible guardar el archivo", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "El archivo es requerido");
        }
    }
}