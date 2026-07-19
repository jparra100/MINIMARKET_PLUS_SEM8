package com.minimarket.service.impl;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.EstadoOrdenCompra;
import com.minimarket.entity.OrdenCompra;
import com.minimarket.entity.Producto;
import com.minimarket.entity.StockSucursal;
import com.minimarket.entity.Sucursal;
import com.minimarket.entity.TipoMovimiento;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.OrdenCompraRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.StockSucursalRepository;
import com.minimarket.repository.SucursalRepository;
import com.minimarket.service.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final SucursalRepository sucursalRepository;
    private final StockSucursalRepository stockRepository;
    private final OrdenCompraRepository ordenCompraRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository,
                                 ProductoRepository productoRepository,
                                 SucursalRepository sucursalRepository,
                                 StockSucursalRepository stockRepository,
                                 OrdenCompraRepository ordenCompraRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
        this.sucursalRepository = sucursalRepository;
        this.stockRepository = stockRepository;
        this.ordenCompraRepository = ordenCompraRepository;
    }

    @Override
    public List<Inventario> findAll() {
        return inventarioRepository.findAll();
    }

    @Override
    public Inventario findById(Long id) {
        return inventarioRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Inventario registrarMovimiento(Long productoId, Long sucursalId,
                                          Integer cantidad, TipoMovimiento tipoMovimiento) {
        if (cantidad == null || cantidad <= 0 || tipoMovimiento == null) {
            throw new IllegalArgumentException("La cantidad debe ser positiva y el tipo es obligatorio");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada"));
        StockSucursal stock = stockRepository.findByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseGet(() -> nuevoStock(producto, sucursal));

        int diferencia = tipoMovimiento == TipoMovimiento.ENTRADA ? cantidad : -cantidad;
        int cantidadSucursal = stock.getCantidad() + diferencia;
        int stockTotal = producto.getStock() + diferencia;
        if (cantidadSucursal < 0 || stockTotal < 0) {
            throw new IllegalStateException("Stock insuficiente para registrar la salida");
        }

        stock.setCantidad(cantidadSucursal);
        producto.setStock(stockTotal);
        stockRepository.save(stock);
        productoRepository.save(producto);
        if (tipoMovimiento == TipoMovimiento.SALIDA) {
            generarOrdenCompraSiCorresponde(stock);
        }

        Inventario movimiento = new Inventario();
        movimiento.setProducto(producto);
        movimiento.setSucursal(sucursal);
        movimiento.setCantidad(cantidad);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        return inventarioRepository.save(movimiento);
    }

    @Override
    public List<Inventario> findByProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    private StockSucursal nuevoStock(Producto producto, Sucursal sucursal) {
        StockSucursal stock = new StockSucursal();
        stock.setProducto(producto);
        stock.setSucursal(sucursal);
        stock.setCantidad(0);
        stock.setStockMinimo(5);
        return stock;
    }

    private void generarOrdenCompraSiCorresponde(StockSucursal stock) {
        if (stock.getCantidad() > stock.getStockMinimo()) {
            return;
        }
        if (stock.getProducto().getProveedor() == null) {
            throw new IllegalStateException(
                    "El producto necesita un proveedor para generar la orden de compra");
        }
        if (stock.getId() != null && ordenCompraRepository
                .existsByStockSucursalIdAndEstado(stock.getId(), EstadoOrdenCompra.PENDIENTE)) {
            return;
        }

        int cantidadObjetivo = Math.max(stock.getStockMinimo() * 2, 1);
        OrdenCompra orden = new OrdenCompra();
        orden.setStockSucursal(stock);
        orden.setProveedor(stock.getProducto().getProveedor());
        orden.setCantidad(Math.max(cantidadObjetivo - stock.getCantidad(), 1));
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setEstado(EstadoOrdenCompra.PENDIENTE);
        ordenCompraRepository.save(orden);
    }
}
