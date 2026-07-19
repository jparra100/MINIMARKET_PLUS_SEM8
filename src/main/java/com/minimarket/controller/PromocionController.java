package com.minimarket.controller;

import com.minimarket.entity.Promocion;
import com.minimarket.service.PromocionService;
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
@RequestMapping("/api/promociones")
@Tag(name = "Promociones", description = "Ofertas y precios promocionales centralizados")
public class PromocionController {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @GetMapping
    @Operation(summary = "Listar promociones")
    public List<EntityModel<Promocion>> listar() {
        return promocionService.findAll().stream().map(this::modelo).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una promocion")
    public ResponseEntity<EntityModel<Promocion>> obtener(@PathVariable Long id) {
        Promocion promocion = promocionService.findById(id);
        return promocion == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(modelo(promocion));
    }

    @GetMapping("/precio/{productoId}")
    @Operation(summary = "Calcular precio vigente de un producto")
    public ResponseEntity<?> calcularPrecio(@PathVariable Long productoId) {
        try {
            return ResponseEntity.ok(promocionService.calcularPrecio(productoId));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear una promocion")
    public ResponseEntity<?> crear(@Valid @RequestBody Promocion promocion) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(modelo(promocionService.save(promocion)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una promocion")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Promocion promocion) {
        if (promocionService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        promocion.setId(id);
        try {
            return ResponseEntity.ok(modelo(promocionService.save(promocion)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una promocion")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (promocionService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        promocionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<Promocion> modelo(Promocion promocion) {
        EntityModel<Promocion> model = EntityModel.of(promocion,
                linkTo(methodOn(PromocionController.class).obtener(promocion.getId())).withSelfRel(),
                linkTo(methodOn(PromocionController.class).listar()).withRel("promociones"));
        if (promocion.getProducto() != null) {
            model.add(linkTo(methodOn(PromocionController.class)
                    .calcularPrecio(promocion.getProducto().getId())).withRel("precio-vigente"));
            model.add(linkTo(methodOn(ProductoController.class)
                    .obtenerProductoPorId(promocion.getProducto().getId())).withRel("producto"));
        }
        return model;
    }
}
