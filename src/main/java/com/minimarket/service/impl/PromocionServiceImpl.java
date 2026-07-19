package com.minimarket.service.impl;

import com.minimarket.dto.PrecioProductoResponse;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Promocion;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.PromocionRepository;
import com.minimarket.service.PromocionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromocionServiceImpl implements PromocionService {

    private final PromocionRepository promocionRepository;
    private final ProductoRepository productoRepository;

    public PromocionServiceImpl(PromocionRepository promocionRepository,
                                ProductoRepository productoRepository) {
        this.promocionRepository = promocionRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Promocion> findAll() {
        return promocionRepository.findAll();
    }

    @Override
    public Promocion findById(Long id) {
        return promocionRepository.findById(id).orElse(null);
    }

    @Override
    public Promocion save(Promocion promocion) {
        validar(promocion);
        return promocionRepository.save(promocion);
    }

    @Override
    public void deleteById(Long id) {
        promocionRepository.deleteById(id);
    }

    @Override
    public PrecioProductoResponse calcularPrecio(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        LocalDateTime ahora = LocalDateTime.now();
        double descuento = promocionRepository
                .findFirstByProductoIdAndActivaTrueAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrderByPorcentajeDescuentoDesc(
                        productoId, ahora, ahora)
                .map(Promocion::getPorcentajeDescuento)
                .orElse(0.0);
        double precioFinal = producto.getPrecio() * (1 - descuento / 100);
        return new PrecioProductoResponse(
                productoId, producto.getPrecio(), descuento,
                Math.round(precioFinal * 100.0) / 100.0);
    }

    private void validar(Promocion promocion) {
        if (promocion.getProducto() == null || promocion.getProducto().getId() == null) {
            throw new IllegalArgumentException("La promoción debe indicar un producto");
        }
        if (promocion.getPorcentajeDescuento() == null
                || promocion.getPorcentajeDescuento() <= 0
                || promocion.getPorcentajeDescuento() > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100");
        }
        if (promocion.getFechaInicio() == null || promocion.getFechaFin() == null
                || !promocion.getFechaFin().isAfter(promocion.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de término debe ser posterior al inicio");
        }
        if (promocion.getActiva() == null) {
            promocion.setActiva(true);
        }
    }
}
