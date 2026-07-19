package com.minimarket.service.impl;

import com.minimarket.dto.CrearPedidoRequest;
import com.minimarket.dto.PrecioProductoResponse;
import com.minimarket.entity.Carrito;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.EstadoPedido;
import com.minimarket.entity.Sucursal;
import com.minimarket.entity.TipoEntrega;
import com.minimarket.entity.TipoMovimiento;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.SucursalRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.InventarioService;
import com.minimarket.service.PromocionService;
import com.minimarket.service.VentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final CarritoRepository carritoRepository;
    private final InventarioService inventarioService;
    private final PromocionService promocionService;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            UsuarioRepository usuarioRepository,
                            SucursalRepository sucursalRepository,
                            CarritoRepository carritoRepository,
                            InventarioService inventarioService,
                            PromocionService promocionService) {
        this.ventaRepository = ventaRepository;
        this.usuarioRepository = usuarioRepository;
        this.sucursalRepository = sucursalRepository;
        this.carritoRepository = carritoRepository;
        this.inventarioService = inventarioService;
        this.promocionService = promocionService;
    }

    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Venta> findByUsername(String username) {
        return ventaRepository.findByUsuarioUsername(username);
    }

    @Override
    @Transactional
    public Venta crearPedido(String username, CrearPedidoRequest request) {
        validarEntrega(request);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Sucursal sucursal = sucursalRepository.findById(request.sucursalId())
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada"));
        List<Carrito> items = carritoRepository.findByUsuarioUsername(username);
        if (items.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        Venta venta = nuevaVenta(usuario, sucursal, request);
        List<DetalleVenta> detalles = new ArrayList<>();
        double total = 0;
        for (Carrito item : items) {
            PrecioProductoResponse precio = promocionService
                    .calcularPrecio(item.getProducto().getId());
            inventarioService.registrarMovimiento(
                    item.getProducto().getId(), sucursal.getId(), item.getCantidad(), TipoMovimiento.SALIDA);
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(precio.precioFinal());
            detalles.add(detalle);
            total += precio.precioFinal() * item.getCantidad();
        }
        venta.setDetalles(detalles);
        venta.setTotal(Math.round(total * 100.0) / 100.0);
        Venta saved = ventaRepository.save(venta);
        carritoRepository.deleteAll(items);
        return saved;
    }

    private Venta nuevaVenta(Usuario usuario, Sucursal sucursal, CrearPedidoRequest request) {
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setSucursal(sucursal);
        venta.setFecha(LocalDateTime.now());
        venta.setTipoEntrega(request.tipoEntrega());
        venta.setDireccionDespacho(request.tipoEntrega() == TipoEntrega.DESPACHO_DOMICILIO
                ? request.direccionDespacho()
                : null);
        venta.setEstado(EstadoPedido.CONFIRMADO);
        return venta;
    }

    private void validarEntrega(CrearPedidoRequest request) {
        if (request == null || request.sucursalId() == null || request.tipoEntrega() == null) {
            throw new IllegalArgumentException("La sucursal y el tipo de entrega son obligatorios");
        }
        if (request.tipoEntrega() == TipoEntrega.DESPACHO_DOMICILIO
                && (request.direccionDespacho() == null || request.direccionDespacho().isBlank())) {
            throw new IllegalArgumentException("Debe indicar la dirección para el despacho");
        }
    }
}
