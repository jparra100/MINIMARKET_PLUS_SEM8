package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.dto.MovimientoInventarioRequest;
import com.minimarket.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Listar inventario", description = "Retorna todos los movimientos de inventario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operacion realizada con exito"),
        @ApiResponse(responseCode = "500", description = "Error interno no controlado en el servidor")
    })
    @GetMapping
    public List<EntityModel<Inventario>> listarMovimientosDeInventario() {
        return inventarioService.findAll().stream()
            .map(inventario -> EntityModel.of(inventario,
                linkTo(methodOn(InventarioController.class).obtenerMovimientoPorId(inventario.getId())).withSelfRel()))
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EntityModel<Inventario> obtenerMovimientoPorId(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        return EntityModel.of(inventario,
            linkTo(methodOn(InventarioController.class).obtenerMovimientoPorId(id)).withSelfRel(),
            linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario()).withRel("lista-inventario"));
    }

    @Operation(summary = "Registrar movimiento", description = "Registra un nuevo movimiento de inventario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado satisfactoriamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud mal formada o con errores en los datos"),
        @ApiResponse(responseCode = "500", description = "Error interno no controlado en el servidor")
    })
    @PostMapping
    public ResponseEntity<?> registrarMovimiento(@RequestBody MovimientoInventarioRequest request) {
        try {
            Inventario saved = inventarioService.registrarMovimiento(
                    request.productoId(), request.sucursalId(),
                    request.cantidad(), request.tipoMovimiento());
            EntityModel<Inventario> model = EntityModel.of(saved,
                    linkTo(methodOn(InventarioController.class)
                            .obtenerMovimientoPorId(saved.getId())).withSelfRel(),
                    linkTo(methodOn(InventarioController.class)
                            .listarMovimientosDeInventario()).withRel("lista-inventario"));
            return ResponseEntity.status(HttpStatus.CREATED).body(model);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
    }
}
