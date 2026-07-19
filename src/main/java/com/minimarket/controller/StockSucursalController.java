package com.minimarket.controller;

import com.minimarket.entity.StockSucursal;
import com.minimarket.service.StockSucursalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/stock")
@Tag(name = "Stock", description = "Disponibilidad y stock minimo por sucursal")
public class StockSucursalController {

    private final StockSucursalService stockService;

    public StockSucursalController(StockSucursalService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    @Operation(summary = "Listar stock de todas las sucursales")
    public List<EntityModel<StockSucursal>> listar() {
        return stockService.findAll().stream().map(this::modelo).toList();
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Consultar disponibilidad de un producto por sucursal")
    public List<EntityModel<StockSucursal>> disponibilidad(@PathVariable Long productoId) {
        return stockService.findByProductoId(productoId).stream().map(this::modelo).toList();
    }

    @GetMapping("/sucursal/{sucursalId}")
    @Operation(summary = "Consultar stock de una sucursal")
    public List<EntityModel<StockSucursal>> stockPorSucursal(@PathVariable Long sucursalId) {
        return stockService.findBySucursalId(sucursalId).stream().map(this::modelo).toList();
    }

    @PatchMapping("/{id}/minimo")
    @Operation(summary = "Actualizar stock minimo", description = "Al alcanzar el minimo se genera una orden de compra")
    public ResponseEntity<?> actualizarMinimo(@PathVariable Long id,
                                               @RequestParam Integer cantidad) {
        try {
            return ResponseEntity.ok(modelo(stockService.actualizarStockMinimo(id, cantidad)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    private EntityModel<StockSucursal> modelo(StockSucursal stock) {
        EntityModel<StockSucursal> model = EntityModel.of(stock,
                linkTo(StockSucursalController.class).slash(stock.getId()).withSelfRel(),
                linkTo(methodOn(StockSucursalController.class).listar()).withRel("stock"),
                linkTo(methodOn(OrdenCompraController.class).listar()).withRel("ordenes-compra"));
        if (stock.getProducto() != null) {
            model.add(linkTo(methodOn(StockSucursalController.class)
                    .disponibilidad(stock.getProducto().getId())).withRel("disponibilidad-producto"));
        }
        if (stock.getSucursal() != null) {
            model.add(linkTo(methodOn(SucursalController.class)
                    .obtener(stock.getSucursal().getId())).withRel("sucursal"));
        }
        return model;
    }
}
