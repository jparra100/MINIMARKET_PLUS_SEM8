package com.minimarket.dto;

import com.minimarket.entity.TipoMovimiento;

public record MovimientoInventarioRequest(
        Long productoId,
        Long sucursalId,
        Integer cantidad,
        TipoMovimiento tipoMovimiento) {
}
