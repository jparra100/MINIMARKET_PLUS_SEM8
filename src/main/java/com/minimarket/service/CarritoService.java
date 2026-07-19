package com.minimarket.service;

import com.minimarket.entity.Carrito;

import java.util.List;

public interface CarritoService {
    List<Carrito> findByUsername(String username);
    Carrito agregar(String username, Long productoId, Integer cantidad);
    void eliminar(String username, Long carritoId);
}
