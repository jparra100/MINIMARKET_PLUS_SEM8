package com.minimarket.controller;

import com.minimarket.dto.CrearPedidoRequest;
import com.minimarket.dto.CrearVentaRequest;
import com.minimarket.entity.TipoEntrega;
import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Consulta y registro de ventas para empleados")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    @Operation(summary = "Listar ventas")
    public List<EntityModel<Venta>> listarVentas() {
        return ventaService.findAll().stream().map(this::modelo).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una venta")
    public ResponseEntity<EntityModel<Venta>> obtenerVentaPorId(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return (venta != null) ? ResponseEntity.ok(modelo(venta)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Registrar una venta", description = "Procesa el carrito del cliente como retiro en tienda")
    public ResponseEntity<?> guardarVenta(@Valid @RequestBody CrearVentaRequest request) {
        try {
            CrearPedidoRequest pedido = new CrearPedidoRequest(
                    request.sucursalId(), TipoEntrega.RETIRO_TIENDA, null);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(modelo(ventaService.crearPedido(request.clienteUsername(), pedido)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
    }

    private EntityModel<Venta> modelo(Venta venta) {
        EntityModel<Venta> model = EntityModel.of(venta,
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(venta.getId())).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"),
                linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withRel("detalles"));
        if (venta.getSucursal() != null) {
            model.add(linkTo(methodOn(SucursalController.class)
                    .obtener(venta.getSucursal().getId())).withRel("sucursal"));
        }
        return model;
    }
}
