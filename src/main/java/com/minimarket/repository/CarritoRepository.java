package com.minimarket.repository;

import com.minimarket.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    List<Carrito> findByUsuarioId(Long usuarioId);
    List<Carrito> findByUsuarioUsername(String username);
    Optional<Carrito> findByUsuarioUsernameAndProductoId(String username, Long productoId);
    Optional<Carrito> findByIdAndUsuarioUsername(Long id, String username);
}
