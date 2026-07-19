package com.minimarket.controller;

import com.minimarket.entity.Promocion;
import com.minimarket.service.PromocionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/promociones")
public class PromocionController {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @GetMapping
    public List<Promocion> listar() {
        return promocionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promocion> obtener(@PathVariable Long id) {
        Promocion promocion = promocionService.findById(id);
        return promocion == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(promocion);
    }

    @GetMapping("/precio/{productoId}")
    public ResponseEntity<?> calcularPrecio(@PathVariable Long productoId) {
        try {
            return ResponseEntity.ok(promocionService.calcularPrecio(productoId));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Promocion promocion) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(promocionService.save(promocion));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Promocion promocion) {
        if (promocionService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        promocion.setId(id);
        try {
            return ResponseEntity.ok(promocionService.save(promocion));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (promocionService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        promocionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
