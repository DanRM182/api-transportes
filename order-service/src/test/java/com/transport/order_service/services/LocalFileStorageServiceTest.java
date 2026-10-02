package com.transport.order_service.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class LocalFileStorageServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldStorePdfSuccessfully() throws Exception {
        LocalFileStorageService service =
                new LocalFileStorageService(
                        tempDir.toString());

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "document.pdf",
                        "application/pdf",
                        "PDF CONTENT".getBytes());

        String filename = service.storePdf(file);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".pdf"));

        assertTrue(
                Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void shouldRejectNonPdfFile() {
        LocalFileStorageService service =
                new LocalFileStorageService(
                        tempDir.toString());

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "image.png",
                        "image/png",
                        "IMAGE".getBytes());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.storePdf(file));
    }

    @Test
    void shouldStoreJpgSuccessfully() throws Exception {
        LocalFileStorageService service =
                new LocalFileStorageService(
                        tempDir.toString()
                );

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "photo.jpg",
                        "image/jpeg",
                        "IMAGE".getBytes()
                );

        String filename =
                service.storeImage(file);

        assertTrue(filename.endsWith(".jpg"));

        assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void shouldStorePngSuccessfully() throws Exception {
        LocalFileStorageService service =
                new LocalFileStorageService(
                        tempDir.toString());

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "photo.png",
                        "image/png",
                        "IMAGE".getBytes());

        String filename =
                service.storeImage(file);

        assertTrue(filename.endsWith(".png"));

        assertTrue(
                Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void shouldRejectInvalidImageType() {
        LocalFileStorageService service =
                new LocalFileStorageService(
                        tempDir.toString());

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "document.pdf",
                        "application/pdf",
                        "PDF".getBytes());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.storeImage(file));
    }
}
