package com.minimarket.controller;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.service.DetalleVentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/detalle-ventas")
@Tag(name = "Detalle de ventas", description = "Lineas de productos vendidas")
public class DetalleVentaController {

    private final DetalleVentaService detalleVentaService;

    public DetalleVentaController(DetalleVentaService detalleVentaService) {
        this.detalleVentaService = detalleVentaService;
    }

    @GetMapping
    @Operation(summary = "Listar detalles de venta")
    public List<EntityModel<DetalleVenta>> listarDetalleVentas() {
        return detalleVentaService.findAll().stream().map(this::modelo).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un detalle de venta")
    public ResponseEntity<EntityModel<DetalleVenta>> obtenerDetalleVentaPorId(@PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        return (detalleVenta != null) ? ResponseEntity.ok(modelo(detalleVenta)) : ResponseEntity.notFound().build();
    }

    private EntityModel<DetalleVenta> modelo(DetalleVenta detalle) {
        EntityModel<DetalleVenta> model = EntityModel.of(detalle,
                linkTo(methodOn(DetalleVentaController.class)
                        .obtenerDetalleVentaPorId(detalle.getId())).withSelfRel(),
                linkTo(methodOn(DetalleVentaController.class)
                        .listarDetalleVentas()).withRel("detalles"));
        if (detalle.getVenta() != null) {
            model.add(linkTo(methodOn(VentaController.class)
                    .obtenerVentaPorId(detalle.getVenta().getId())).withRel("venta"));
        }
        return model;
    }
}
