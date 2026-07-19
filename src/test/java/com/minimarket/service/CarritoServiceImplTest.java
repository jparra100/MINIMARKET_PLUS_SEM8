package com.minimarket.service;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.impl.CarritoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ProductoRepository productoRepository;
    @InjectMocks
    private CarritoServiceImpl service;

    @Test
    void agregaProductoAlCarritoDelUsuarioAutenticado() {
        Usuario usuario = new Usuario();
        Producto producto = new Producto();
        producto.setId(1L);
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoRepository.findByUsuarioUsernameAndProductoId("cliente", 1L))
                .thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Carrito item = service.agregar("cliente", 1L, 2);

        assertEquals(2, item.getCantidad());
        assertEquals(usuario, item.getUsuario());
        assertEquals(producto, item.getProducto());
    }

    @Test
    void rechazaCantidadesInvalidas() {
        assertThrows(IllegalArgumentException.class,
                () -> service.agregar("cliente", 1L, 0));
    }
}
