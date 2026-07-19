package com.minimarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearVentaRequest(
        @NotBlank(message = "El cliente es obligatorio") String clienteUsername,
        @NotNull(message = "La sucursal es obligatoria") Long sucursalId) {
}
