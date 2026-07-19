package com.minimarket.controller;

import com.minimarket.entity.Proveedor;
import com.minimarket.service.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/proveedores")
@Tag(name = "Proveedores", description = "Proveedores utilizados por las ordenes de compra")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    @Operation(summary = "Listar proveedores")
    public List<EntityModel<Proveedor>> listar() {
        return proveedorService.findAll().stream().map(this::modelo).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un proveedor")
    public ResponseEntity<EntityModel<Proveedor>> obtener(@PathVariable Long id) {
        Proveedor proveedor = proveedorService.findById(id);
        return proveedor == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(modelo(proveedor));
    }

    @PostMapping
    @Operation(summary = "Crear un proveedor")
    public ResponseEntity<EntityModel<Proveedor>> crear(@Valid @RequestBody Proveedor proveedor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(modelo(proveedorService.save(proveedor)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un proveedor")
    public ResponseEntity<EntityModel<Proveedor>> actualizar(@PathVariable Long id,
                                                              @Valid @RequestBody Proveedor proveedor) {
        if (proveedorService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        proveedor.setId(id);
        return ResponseEntity.ok(modelo(proveedorService.save(proveedor)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un proveedor")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (proveedorService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        proveedorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<Proveedor> modelo(Proveedor proveedor) {
        return EntityModel.of(proveedor,
                linkTo(methodOn(ProveedorController.class).obtener(proveedor.getId())).withSelfRel(),
                linkTo(methodOn(ProveedorController.class).listar()).withRel("proveedores"),
                linkTo(methodOn(OrdenCompraController.class).listar()).withRel("ordenes-compra"));
    }
}
