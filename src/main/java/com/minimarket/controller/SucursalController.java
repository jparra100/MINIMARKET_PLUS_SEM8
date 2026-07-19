package com.minimarket.controller;

import com.minimarket.entity.Sucursal;
import com.minimarket.service.SucursalService;
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
@RequestMapping("/api/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @GetMapping
    public List<Sucursal> listar() {
        return sucursalService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sucursal> obtener(@PathVariable Long id) {
        Sucursal sucursal = sucursalService.findById(id);
        return sucursal == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(sucursal);
    }

    @PostMapping
    public ResponseEntity<Sucursal> crear(@RequestBody Sucursal sucursal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalService.save(sucursal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sucursal> actualizar(@PathVariable Long id, @RequestBody Sucursal sucursal) {
        if (sucursalService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        sucursal.setId(id);
        return ResponseEntity.ok(sucursalService.save(sucursal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (sucursalService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        sucursalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
