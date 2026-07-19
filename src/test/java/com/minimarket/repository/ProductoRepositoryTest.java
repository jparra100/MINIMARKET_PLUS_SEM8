package com.minimarket.repository;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.EstadoPedido;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Sucursal;
import com.minimarket.entity.TipoEntrega;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void reporteIncluyeProductosVendidosYNoVendidos() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Abarrotes");
        entityManager.persist(categoria);

        Producto vendido = producto("Arroz", categoria);
        Producto noVendido = producto("Fideos", categoria);
        entityManager.persist(vendido);
        entityManager.persist(noVendido);

        Usuario usuario = new Usuario();
        usuario.setUsername("comprador");
        usuario.setPassword("clave-cifrada");
        entityManager.persist(usuario);
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Centro");
        sucursal.setDireccion("Santiago");
        entityManager.persist(sucursal);

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setSucursal(sucursal);
        venta.setFecha(LocalDateTime.now());
        venta.setTipoEntrega(TipoEntrega.RETIRO_TIENDA);
        venta.setEstado(EstadoPedido.CONFIRMADO);
        venta.setTotal(3000.0);
        entityManager.persist(venta);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setProducto(vendido);
        detalle.setCantidad(3);
        detalle.setPrecio(1000.0);
        entityManager.persistAndFlush(detalle);

        List<Object[]> rotacion = productoRepository.findRotacionProductos();

        assertEquals("Arroz", rotacion.get(0)[1]);
        assertEquals(3L, ((Number) rotacion.get(0)[2]).longValue());
        assertEquals("Fideos", rotacion.get(1)[1]);
        assertEquals(0L, ((Number) rotacion.get(1)[2]).longValue());
    }

    private Producto producto(String nombre, Categoria categoria) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(1000.0);
        producto.setStock(10);
        producto.setCategoria(categoria);
        return producto;
    }
}
