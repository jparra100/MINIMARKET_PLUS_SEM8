package com.minimarket.service.impl;

import com.minimarket.entity.EstadoOrdenCompra;
import com.minimarket.entity.OrdenCompra;
import com.minimarket.entity.StockSucursal;
import com.minimarket.entity.TipoMovimiento;
import com.minimarket.repository.OrdenCompraRepository;
import com.minimarket.service.InventarioService;
import com.minimarket.service.OrdenCompraService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final InventarioService inventarioService;

    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository,
                                  InventarioService inventarioService) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.inventarioService = inventarioService;
    }

    @Override
    public List<OrdenCompra> findAll() {
        return ordenCompraRepository.findAll();
    }

    @Override
    public OrdenCompra findById(Long id) {
        return ordenCompraRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public OrdenCompra recibir(Long id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden de compra no encontrada"));
        if (orden.getEstado() == EstadoOrdenCompra.RECIBIDA) {
            throw new IllegalStateException("La orden ya fue recibida");
        }

        StockSucursal stock = orden.getStockSucursal();
        inventarioService.registrarMovimiento(
                stock.getProducto().getId(), stock.getSucursal().getId(),
                orden.getCantidad(), TipoMovimiento.ENTRADA);
        orden.setEstado(EstadoOrdenCompra.RECIBIDA);
        orden.setFechaRecepcion(LocalDateTime.now());
        return ordenCompraRepository.save(orden);
    }
}
