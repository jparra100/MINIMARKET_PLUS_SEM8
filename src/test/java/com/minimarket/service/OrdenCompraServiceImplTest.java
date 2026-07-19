package com.minimarket.service;

import com.minimarket.entity.EstadoOrdenCompra;
import com.minimarket.entity.OrdenCompra;
import com.minimarket.entity.Producto;
import com.minimarket.entity.StockSucursal;
import com.minimarket.entity.Sucursal;
import com.minimarket.entity.TipoMovimiento;
import com.minimarket.repository.OrdenCompraRepository;
import com.minimarket.service.impl.OrdenCompraServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdenCompraServiceImplTest {

    @Mock
    private OrdenCompraRepository ordenCompraRepository;
    @Mock
    private InventarioService inventarioService;

    private OrdenCompraServiceImpl service;
    private OrdenCompra orden;

    @BeforeEach
    void setUp() {
        service = new OrdenCompraServiceImpl(ordenCompraRepository, inventarioService);
        Producto producto = new Producto();
        producto.setId(10L);
        Sucursal sucursal = new Sucursal();
        sucursal.setId(20L);
        StockSucursal stock = new StockSucursal();
        stock.setProducto(producto);
        stock.setSucursal(sucursal);
        orden = new OrdenCompra();
        orden.setId(1L);
        orden.setStockSucursal(stock);
        orden.setCantidad(7);
    }

    @Test
    void recibirOrdenRegistraLaEntradaDeInventario() {
        orden.setEstado(EstadoOrdenCompra.PENDIENTE);
        when(ordenCompraRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenCompraRepository.save(orden)).thenReturn(orden);

        OrdenCompra recibida = service.recibir(1L);

        verify(inventarioService).registrarMovimiento(10L, 20L, 7, TipoMovimiento.ENTRADA);
        assertEquals(EstadoOrdenCompra.RECIBIDA, recibida.getEstado());
        assertNotNull(recibida.getFechaRecepcion());
    }

    @Test
    void noPermiteRecibirDosVecesLaMismaOrden() {
        orden.setEstado(EstadoOrdenCompra.RECIBIDA);
        when(ordenCompraRepository.findById(1L)).thenReturn(Optional.of(orden));

        assertThrows(IllegalStateException.class, () -> service.recibir(1L));

        verify(inventarioService, never())
                .registrarMovimiento(10L, 20L, 7, TipoMovimiento.ENTRADA);
    }
}
