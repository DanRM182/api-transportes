package com.transport.driver_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDriverRequest(
         @Schema(description = "Nombre del conductor", example = "Juan Carlos Torres Melgar")
         @NotBlank(message = "El nombre es requerido")
         @Size(max = 150, message = "El nombre debe tener máximo 150 caracteres")
         String name,

         @Schema(description = "Número de licencia del conductor", example = "2332ACCRT1")
         @NotBlank(message = "El número de licencia es requerido")
         @Size(max = 30, message = "El número de licencia debe tener máximo 30 caracteres")
         String licenseNumber
) { }