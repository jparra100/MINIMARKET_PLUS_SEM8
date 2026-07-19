package com.minimarket.service;

import com.minimarket.dto.PrecioProductoResponse;
import com.minimarket.entity.Promocion;

import java.util.List;

public interface PromocionService {
    List<Promocion> findAll();
    Promocion findById(Long id);
    Promocion save(Promocion promocion);
    void deleteById(Long id);
    PrecioProductoResponse calcularPrecio(Long productoId);
}
