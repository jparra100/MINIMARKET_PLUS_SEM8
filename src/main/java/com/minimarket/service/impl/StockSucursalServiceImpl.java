package com.minimarket.service.impl;

import com.minimarket.entity.StockSucursal;
import com.minimarket.repository.StockSucursalRepository;
import com.minimarket.service.StockSucursalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockSucursalServiceImpl implements StockSucursalService {

    private final StockSucursalRepository stockRepository;

    public StockSucursalServiceImpl(StockSucursalRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<StockSucursal> findAll() {
        return stockRepository.findAll();
    }

    @Override
    public List<StockSucursal> findByProductoId(Long productoId) {
        return stockRepository.findByProductoId(productoId);
    }

    @Override
    public List<StockSucursal> findBySucursalId(Long sucursalId) {
        return stockRepository.findBySucursalId(sucursalId);
    }

    @Override
    public StockSucursal actualizarStockMinimo(Long id, Integer stockMinimo) {
        if (stockMinimo == null || stockMinimo < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        }
        StockSucursal stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de stock no encontrado"));
        stock.setStockMinimo(stockMinimo);
        return stockRepository.save(stock);
    }
}
