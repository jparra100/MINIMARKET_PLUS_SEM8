package com.minimarket.dto;

import com.minimarket.entity.TipoEntrega;
import jakarta.validation.constraints.NotNull;

public record CrearPedidoRequest(
        @NotNull(message = "La sucursal es obligatoria") Long sucursalId,
        @NotNull(message = "El tipo de entrega es obligatorio") TipoEntrega tipoEntrega,
        String direccionDespacho) {
}
