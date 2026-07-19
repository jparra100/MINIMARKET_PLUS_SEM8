package com.minimarket.service;

import com.minimarket.dto.PrecioProductoResponse;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Promocion;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.PromocionRepository;
import com.minimarket.service.impl.PromocionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromocionServiceImplTest {

    @Mock
    private PromocionRepository promocionRepository;
    @Mock
    private ProductoRepository productoRepository;

    private PromocionServiceImpl service;
    private Producto producto;

    @BeforeEach
    void setUp() {
        service = new PromocionServiceImpl(promocionRepository, productoRepository);
        producto = new Producto();
        producto.setId(1L);
        producto.setPrecio(1000.0);
    }

    @Test
    void aplicaLaPromocionVigenteConMayorDescuento() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        Promocion promocion = new Promocion();
        promocion.setPorcentajeDescuento(25.0);
        when(promocionRepository
                .findFirstByProductoIdAndActivaTrueAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrderByPorcentajeDescuentoDesc(
                        eq(1L), any(), any()))
                .thenReturn(Optional.of(promocion));

        PrecioProductoResponse precio = service.calcularPrecio(1L);

        assertEquals(1000.0, precio.precioNormal());
        assertEquals(25.0, precio.porcentajeDescuento());
        assertEquals(750.0, precio.precioFinal());
    }

    @Test
    void conservaElPrecioCuandoNoHayPromocionVigente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(promocionRepository
                .findFirstByProductoIdAndActivaTrueAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrderByPorcentajeDescuentoDesc(
                        eq(1L), any(), any()))
                .thenReturn(Optional.empty());

        PrecioProductoResponse precio = service.calcularPrecio(1L);

        assertEquals(0.0, precio.porcentajeDescuento());
        assertEquals(1000.0, precio.precioFinal());
    }

    @Test
    void rechazaDescuentosFueraDeRango() {
        Promocion promocion = new Promocion();
        promocion.setProducto(producto);
        promocion.setPorcentajeDescuento(120.0);

        assertThrows(IllegalArgumentException.class, () -> service.save(promocion));
    }
}
