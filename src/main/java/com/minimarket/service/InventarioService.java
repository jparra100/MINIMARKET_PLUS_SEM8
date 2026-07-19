package com.minimarket.service;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.TipoMovimiento;

import java.util.List;

public interface InventarioService {
    List<Inventario> findAll();
    Inventario findById(Long id);
    Inventario registrarMovimiento(Long productoId, Long sucursalId,
                                   Integer cantidad, TipoMovimiento tipoMovimiento);
    List<Inventario> findByProductoId(Long productoId);
}
