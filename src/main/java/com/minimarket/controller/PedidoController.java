package com.minimarket.controller;

import com.minimarket.dto.CrearPedidoRequest;
import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Pedidos del cliente para retiro en tienda o despacho")
public class PedidoController {

    private final VentaService ventaService;

    public PedidoController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    @Operation(summary = "Listar mis pedidos")
    public List<EntityModel<Venta>> misPedidos(Principal principal) {
        return ventaService.findByUsername(principal.getName()).stream().map(this::modelo).toList();
    }

    @PostMapping
    @Operation(summary = "Crear un pedido", description = "Convierte el carrito del cliente en un pedido y descuenta stock")
    public ResponseEntity<?> crear(@Valid @RequestBody CrearPedidoRequest request, Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(modelo(ventaService.crearPedido(principal.getName(), request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
    }

    private EntityModel<Venta> modelo(Venta venta) {
        return EntityModel.of(venta,
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(venta.getId())).withSelfRel(),
                linkTo(PedidoController.class).withRel("mis-pedidos"),
                linkTo(methodOn(SucursalController.class)
                        .obtener(venta.getSucursal().getId())).withRel("sucursal"));
    }
}
