package com.minimarket.controller;

import com.minimarket.dto.CrearPedidoRequest;
import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final VentaService ventaService;

    public PedidoController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<Venta> misPedidos(Principal principal) {
        return ventaService.findByUsername(principal.getName());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearPedidoRequest request, Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ventaService.crearPedido(principal.getName(), request));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
    }
}
