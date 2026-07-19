package com.minimarket.controller;

import com.minimarket.dto.ReporteRotacionResponse;
import com.minimarket.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reportes protegidos para la administracion")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/rotacion-productos")
    @Operation(summary = "Reporte de rotacion", description = "Muestra los productos mas y menos vendidos")
    public EntityModel<ReporteRotacionResponse> rotacionProductos() {
        return EntityModel.of(reporteService.obtenerRotacionProductos(),
                linkTo(methodOn(ReporteController.class).rotacionProductos()).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"));
    }
}
