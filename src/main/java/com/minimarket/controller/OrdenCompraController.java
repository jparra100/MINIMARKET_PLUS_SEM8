package com.minimarket.controller;

import com.minimarket.entity.OrdenCompra;
import com.minimarket.service.OrdenCompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/ordenes-compra")
@Tag(name = "Ordenes de compra", description = "Ordenes generadas automaticamente por stock minimo")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    public OrdenCompraController(OrdenCompraService ordenCompraService) {
        this.ordenCompraService = ordenCompraService;
    }

    @GetMapping
    @Operation(summary = "Listar ordenes de compra")
    public List<EntityModel<OrdenCompra>> listar() {
        return ordenCompraService.findAll().stream().map(this::modelo).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una orden de compra")
    public ResponseEntity<EntityModel<OrdenCompra>> obtener(@PathVariable Long id) {
        OrdenCompra orden = ordenCompraService.findById(id);
        return orden == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(modelo(orden));
    }

    @PatchMapping("/{id}/recibir")
    @Operation(summary = "Recibir una orden", description = "Marca la orden como recibida y aumenta el stock de la sucursal")
    public ResponseEntity<?> recibir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(modelo(ordenCompraService.recibir(id)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
    }

    private EntityModel<OrdenCompra> modelo(OrdenCompra orden) {
        EntityModel<OrdenCompra> model = EntityModel.of(orden,
                linkTo(methodOn(OrdenCompraController.class).obtener(orden.getId())).withSelfRel(),
                linkTo(methodOn(OrdenCompraController.class).listar()).withRel("ordenes-compra"));
        if (orden.getProveedor() != null) {
            model.add(linkTo(methodOn(ProveedorController.class)
                    .obtener(orden.getProveedor().getId())).withRel("proveedor"));
        }
        if (orden.getStockSucursal() != null && orden.getStockSucursal().getSucursal() != null) {
            model.add(linkTo(methodOn(StockSucursalController.class)
                    .stockPorSucursal(orden.getStockSucursal().getSucursal().getId())).withRel("stock-sucursal"));
        }
        return model;
    }
}
