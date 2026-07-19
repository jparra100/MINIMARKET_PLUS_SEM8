package com.minimarket.controller;

import com.minimarket.entity.OrdenCompra;
import com.minimarket.service.OrdenCompraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-compra")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    public OrdenCompraController(OrdenCompraService ordenCompraService) {
        this.ordenCompraService = ordenCompraService;
    }

    @GetMapping
    public List<OrdenCompra> listar() {
        return ordenCompraService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompra> obtener(@PathVariable Long id) {
        OrdenCompra orden = ordenCompraService.findById(id);
        return orden == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(orden);
    }

    @PatchMapping("/{id}/recibir")
    public ResponseEntity<?> recibir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ordenCompraService.recibir(id));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
    }
}
