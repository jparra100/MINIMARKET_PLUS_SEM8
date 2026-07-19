package com.minimarket.repository;

import com.minimarket.entity.EstadoOrdenCompra;
import com.minimarket.entity.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    boolean existsByStockSucursalIdAndEstado(Long stockSucursalId, EstadoOrdenCompra estado);
}
