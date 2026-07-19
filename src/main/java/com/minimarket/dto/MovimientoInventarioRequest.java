package com.minimarket.dto;

import com.minimarket.entity.TipoMovimiento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimientoInventarioRequest(
        @NotNull(message = "El producto es obligatorio") Long productoId,
        @NotNull(message = "La sucursal es obligatoria") Long sucursalId,
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser positiva") Integer cantidad,
        @NotNull(message = "El tipo de movimiento es obligatorio") TipoMovimiento tipoMovimiento) {
}
