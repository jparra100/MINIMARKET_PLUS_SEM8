package com.minimarket.service;

import com.minimarket.dto.ReporteRotacionResponse;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.ReporteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;
    @InjectMocks
    private ReporteServiceImpl service;

    @Test
    void identificaProductosConMayorYMenorRotacion() {
        when(productoRepository.findRotacionProductos()).thenReturn(List.of(
                new Object[]{1L, "Leche", 25L},
                new Object[]{2L, "Arroz", 10L},
                new Object[]{3L, "Jabón", 0L}));

        ReporteRotacionResponse reporte = service.obtenerRotacionProductos();

        assertEquals("Leche", reporte.masVendido().producto());
        assertEquals(25L, reporte.masVendido().unidadesVendidas());
        assertEquals("Jabón", reporte.menosVendido().producto());
        assertEquals(0L, reporte.menosVendido().unidadesVendidas());
        assertEquals(3, reporte.productos().size());
    }

    @Test
    void entregaReporteVacioCuandoNoHayProductos() {
        when(productoRepository.findRotacionProductos()).thenReturn(List.of());

        ReporteRotacionResponse reporte = service.obtenerRotacionProductos();

        assertNull(reporte.masVendido());
        assertNull(reporte.menosVendido());
        assertEquals(0, reporte.productos().size());
    }
}
