package com.minimarket.controller;

import com.minimarket.entity.Proveedor;
import com.minimarket.service.ProveedorService;
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
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public List<Proveedor> listar() {
        return proveedorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtener(@PathVariable Long id) {
        Proveedor proveedor = proveedorService.findById(id);
        return proveedor == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(proveedor);
    }

    @PostMapping
    public ResponseEntity<Proveedor> crear(@RequestBody Proveedor proveedor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.save(proveedor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Long id,
                                                 @RequestBody Proveedor proveedor) {
        if (proveedorService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        proveedor.setId(id);
        return ResponseEntity.ok(proveedorService.save(proveedor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (proveedorService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        proveedorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
