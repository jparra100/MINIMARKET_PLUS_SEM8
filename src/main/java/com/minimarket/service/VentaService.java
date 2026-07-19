package com.minimarket.service;

import com.minimarket.dto.CrearPedidoRequest;
import com.minimarket.entity.Venta;

import java.util.List;

public interface VentaService {
    List<Venta> findAll();
    Venta findById(Long id);
    List<Venta> findByUsername(String username);
    Venta crearPedido(String username, CrearPedidoRequest request);
}
