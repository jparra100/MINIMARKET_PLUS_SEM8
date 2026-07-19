package com.minimarket.repository;

import com.minimarket.entity.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    Optional<Promocion> findFirstByProductoIdAndActivaTrueAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrderByPorcentajeDescuentoDesc(
            Long productoId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
