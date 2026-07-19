package com.minimarket.service;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.StockSucursal;
import com.minimarket.entity.Sucursal;
import com.minimarket.entity.TipoMovimiento;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.StockSucursalRepository;
import com.minimarket.repository.SucursalRepository;
import com.minimarket.service.impl.InventarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @Mock
    private StockSucursalRepository stockRepository;

    private InventarioServiceImpl service;
    private Producto producto;
    private Sucursal sucursal;
    private StockSucursal stockSucursal;

    @BeforeEach
    void setUp() {
        service = new InventarioServiceImpl(
                inventarioRepository, productoRepository, sucursalRepository, stockRepository);
        producto = new Producto();
        producto.setId(1L);
        producto.setStock(10);
        sucursal = new Sucursal();
        sucursal.setId(1L);
        stockSucursal = new StockSucursal();
        stockSucursal.setProducto(producto);
        stockSucursal.setSucursal(sucursal);
        stockSucursal.setCantidad(5);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(stockRepository.findByProductoIdAndSucursalId(1L, 1L))
                .thenReturn(Optional.of(stockSucursal));
    }

    @Test
    void registraEntradaYActualizaStockDeSucursalYTotal() {
        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Inventario movimiento = service.registrarMovimiento(1L, 1L, 3, TipoMovimiento.ENTRADA);

        assertEquals(8, stockSucursal.getCantidad());
        assertEquals(13, producto.getStock());
        assertEquals(TipoMovimiento.ENTRADA, movimiento.getTipoMovimiento());
        verify(stockRepository).save(stockSucursal);
        verify(productoRepository).save(producto);
    }

    @Test
    void rechazaSalidaCuandoLaSucursalNoTieneStock() {
        assertThrows(IllegalStateException.class,
                () -> service.registrarMovimiento(1L, 1L, 6, TipoMovimiento.SALIDA));

        assertEquals(5, stockSucursal.getCantidad());
        assertEquals(10, producto.getStock());
        verify(inventarioRepository, never()).save(any());
    }
}
