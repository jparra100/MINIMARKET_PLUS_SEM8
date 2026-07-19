package com.minimarket.controller;

import com.minimarket.entity.StockSucursal;
import com.minimarket.service.StockSucursalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockSucursalController {

    private final StockSucursalService stockService;

    public StockSucursalController(StockSucursalService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<StockSucursal> listar() {
        return stockService.findAll();
    }

    @GetMapping("/producto/{productoId}")
    public List<StockSucursal> disponibilidad(@PathVariable Long productoId) {
        return stockService.findByProductoId(productoId);
    }

    @GetMapping("/sucursal/{sucursalId}")
    public List<StockSucursal> stockPorSucursal(@PathVariable Long sucursalId) {
        return stockService.findBySucursalId(sucursalId);
    }

    @PatchMapping("/{id}/minimo")
    public ResponseEntity<?> actualizarMinimo(@PathVariable Long id,
                                               @RequestParam Integer cantidad) {
        try {
            return ResponseEntity.ok(stockService.actualizarStockMinimo(id, cantidad));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
