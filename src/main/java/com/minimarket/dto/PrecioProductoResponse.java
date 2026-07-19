package com.minimarket.dto;

public record PrecioProductoResponse(
        Long productoId,
        Double precioNormal,
        Double porcentajeDescuento,
        Double precioFinal) {
}
