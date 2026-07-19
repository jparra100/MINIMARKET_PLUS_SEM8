package com.minimarket.dto;

import com.minimarket.entity.TipoEntrega;

public record CrearPedidoRequest(
        Long sucursalId,
        TipoEntrega tipoEntrega,
        String direccionDespacho) {
}
