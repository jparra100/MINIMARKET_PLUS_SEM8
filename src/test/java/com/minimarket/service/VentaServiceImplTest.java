package com.minimarket.service;

import com.minimarket.dto.CrearPedidoRequest;
import com.minimarket.dto.PrecioProductoResponse;
import com.minimarket.entity.Carrito;
import com.minimarket.entity.EstadoPedido;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Sucursal;
import com.minimarket.entity.TipoEntrega;
import com.minimarket.entity.TipoMovimiento;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.SucursalRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.VentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @Mock
    private CarritoRepository carritoRepository;
    @Mock
    private InventarioService inventarioService;
    @Mock
    private PromocionService promocionService;

    private VentaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VentaServiceImpl(ventaRepository, usuarioRepository, sucursalRepository,
                carritoRepository, inventarioService, promocionService);
    }

    @Test
    void confirmaPedidoConPrecioPromocionalYDescuentaStock() {
        Usuario usuario = new Usuario();
        Sucursal sucursal = new Sucursal();
        sucursal.setId(2L);
        Producto producto = new Producto();
        producto.setId(3L);
        Carrito item = new Carrito();
        item.setProducto(producto);
        item.setCantidad(2);
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(usuario));
        when(sucursalRepository.findById(2L)).thenReturn(Optional.of(sucursal));
        when(carritoRepository.findByUsuarioUsername("cliente")).thenReturn(List.of(item));
        when(promocionService.calcularPrecio(3L))
                .thenReturn(new PrecioProductoResponse(3L, 1000.0, 20.0, 800.0));
        when(ventaRepository.save(any(Venta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Venta venta = service.crearPedido("cliente",
                new CrearPedidoRequest(2L, TipoEntrega.RETIRO_TIENDA, null));

        assertEquals(1600.0, venta.getTotal());
        assertEquals(EstadoPedido.CONFIRMADO, venta.getEstado());
        assertNull(venta.getDireccionDespacho());
        assertEquals(1, venta.getDetalles().size());
        assertEquals(800.0, venta.getDetalles().get(0).getPrecio());
        verify(inventarioService).registrarMovimiento(3L, 2L, 2, TipoMovimiento.SALIDA);
        verify(carritoRepository).deleteAll(List.of(item));
    }

    @Test
    void exigeDireccionCuandoElPedidoEsConDespacho() {
        assertThrows(IllegalArgumentException.class,
                () -> service.crearPedido("cliente",
                        new CrearPedidoRequest(2L, TipoEntrega.DESPACHO_DOMICILIO, " ")));
    }

    @Test
    void rechazaPedidoConCarritoVacio() {
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(new Usuario()));
        when(sucursalRepository.findById(2L)).thenReturn(Optional.of(new Sucursal()));
        when(carritoRepository.findByUsuarioUsername("cliente")).thenReturn(List.of());

        assertThrows(IllegalStateException.class,
                () -> service.crearPedido("cliente",
                        new CrearPedidoRequest(2L, TipoEntrega.RETIRO_TIENDA, null)));
    }
}
