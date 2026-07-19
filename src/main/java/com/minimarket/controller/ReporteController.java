package com.minimarket.controller;

import com.minimarket.dto.ReporteRotacionResponse;
import com.minimarket.service.ReporteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/rotacion-productos")
    public ReporteRotacionResponse rotacionProductos() {
        return reporteService.obtenerRotacionProductos();
    }
}
