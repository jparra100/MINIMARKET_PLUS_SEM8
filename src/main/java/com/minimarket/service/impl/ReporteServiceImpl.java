package com.minimarket.service.impl;

import com.minimarket.dto.ReporteRotacionResponse;
import com.minimarket.dto.RotacionProductoResponse;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.ReporteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ProductoRepository productoRepository;

    public ReporteServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public ReporteRotacionResponse obtenerRotacionProductos() {
        List<RotacionProductoResponse> productos = productoRepository.findRotacionProductos().stream()
                .map(this::mapear)
                .toList();
        if (productos.isEmpty()) {
            return new ReporteRotacionResponse(null, null, productos);
        }
        return new ReporteRotacionResponse(
                productos.get(0), productos.get(productos.size() - 1), productos);
    }

    private RotacionProductoResponse mapear(Object[] fila) {
        return new RotacionProductoResponse(
                ((Number) fila[0]).longValue(),
                fila[1].toString(),
                ((Number) fila[2]).longValue());
    }
}
