package com.minimarket.controller;

import com.minimarket.entity.Sucursal;
import com.minimarket.service.SucursalService;
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
@RequestMapping("/api/sucursales")
@Tag(name = "Sucursales", description = "Administracion de las sucursales del minimarket")
public class SucursalController {

    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @GetMapping
    @Operation(summary = "Listar sucursales")
    public List<EntityModel<Sucursal>> listar() {
        return sucursalService.findAll().stream().map(this::modelo).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una sucursal")
    public ResponseEntity<EntityModel<Sucursal>> obtener(@PathVariable Long id) {
        Sucursal sucursal = sucursalService.findById(id);
        return sucursal == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(modelo(sucursal));
    }

    @PostMapping
    @Operation(summary = "Crear una sucursal")
    public ResponseEntity<EntityModel<Sucursal>> crear(@Valid @RequestBody Sucursal sucursal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(modelo(sucursalService.save(sucursal)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una sucursal")
    public ResponseEntity<EntityModel<Sucursal>> actualizar(@PathVariable Long id,
                                                             @Valid @RequestBody Sucursal sucursal) {
        if (sucursalService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        sucursal.setId(id);
        return ResponseEntity.ok(modelo(sucursalService.save(sucursal)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una sucursal")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (sucursalService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        sucursalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<Sucursal> modelo(Sucursal sucursal) {
        return EntityModel.of(sucursal,
                linkTo(methodOn(SucursalController.class).obtener(sucursal.getId())).withSelfRel(),
                linkTo(methodOn(SucursalController.class).listar()).withRel("sucursales"),
                linkTo(methodOn(StockSucursalController.class)
                        .stockPorSucursal(sucursal.getId())).withRel("stock"));
    }
}
