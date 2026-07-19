package com.minimarket.service.impl;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.CarritoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository,
                              UsuarioRepository usuarioRepository,
                              ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Carrito> findByUsername(String username) {
        return carritoRepository.findByUsuarioUsername(username);
    }

    @Override
    @Transactional
    public Carrito agregar(String username, Long productoId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        Carrito carrito = carritoRepository
                .findByUsuarioUsernameAndProductoId(username, productoId)
                .orElseGet(() -> nuevoItem(usuario, producto));
        carrito.setCantidad(carrito.getCantidad() + cantidad);
        return carritoRepository.save(carrito);
    }

    @Override
    public void eliminar(String username, Long carritoId) {
        Carrito carrito = carritoRepository.findByIdAndUsuarioUsername(carritoId, username)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en el carrito"));
        carritoRepository.delete(carrito);
    }

    private Carrito nuevoItem(Usuario usuario, Producto producto) {
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(0);
        return carrito;
    }
}
