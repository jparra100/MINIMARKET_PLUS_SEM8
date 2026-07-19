package com.minimarket.dto;

public record RotacionProductoResponse(
        Long productoId,
        String producto,
        Long unidadesVendidas) {
}
