package com.minimarket.controller;

import com.minimarket.dto.CrearPedidoRequest;
import com.minimarket.dto.CrearVentaRequest;
import com.minimarket.entity.TipoEntrega;
import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return (venta != null) ? ResponseEntity.ok(venta) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> guardarVenta(@RequestBody CrearVentaRequest request) {
        try {
            CrearPedidoRequest pedido = new CrearPedidoRequest(
                    request.sucursalId(), TipoEntrega.RETIRO_TIENDA, null);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ventaService.crearPedido(request.clienteUsername(), pedido));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
    }
}
