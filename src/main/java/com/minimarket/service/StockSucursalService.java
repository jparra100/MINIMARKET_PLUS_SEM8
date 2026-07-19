package com.minimarket.service;

import com.minimarket.entity.StockSucursal;

import java.util.List;

public interface StockSucursalService {
    List<StockSucursal> findAll();
    List<StockSucursal> findByProductoId(Long productoId);
    List<StockSucursal> findBySucursalId(Long sucursalId);
    StockSucursal actualizarStockMinimo(Long id, Integer stockMinimo);
}
