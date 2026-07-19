package com.minimarket.repository;

import com.minimarket.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaId(Long categoriaId);

    @Query(value = """
            SELECT p.id, p.nombre, COALESCE(SUM(d.cantidad), 0) AS unidades
            FROM producto p
            LEFT JOIN detalle_venta d ON d.producto_id = p.id
            GROUP BY p.id, p.nombre
            ORDER BY unidades DESC, p.nombre ASC
            """, nativeQuery = true)
    List<Object[]> findRotacionProductos();
}
