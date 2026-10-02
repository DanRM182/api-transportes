package com.transport.order_service.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storePdf(MultipartFile file);

    String storeImage(MultipartFile file);
}
