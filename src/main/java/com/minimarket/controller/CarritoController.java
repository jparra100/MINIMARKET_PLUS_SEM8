package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
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
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(summary = "Listar carrito", description = "Retorna todos los items del carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operacion realizada con exito"),
        @ApiResponse(responseCode = "500", description = "Error interno no controlado en el servidor")
    })
    @GetMapping
    public List<EntityModel<Carrito>> listarCarrito() {
        return carritoService.findAll().stream()
            .map(carrito -> EntityModel.of(carrito,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(carrito.getId())).withSelfRel()))
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EntityModel<Carrito> obtenerCarritoPorId(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        return EntityModel.of(carrito,
            linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(id)).withSelfRel(),
            linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("lista-carrito"));
    }

    @Operation(summary = "Agregar al carrito", description = "Agrega un producto al carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado satisfactoriamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud mal formada o con errores en los datos"),
        @ApiResponse(responseCode = "500", description = "Error interno no controlado en el servidor")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Carrito>> agregarProductoAlCarrito(@RequestBody Carrito carrito) {
        Carrito saved = carritoService.save(carrito);
        EntityModel<Carrito> model = EntityModel.of(saved,
            linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(saved.getId())).withSelfRel(),
            linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("lista-carrito"));
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizarCarrito(@PathVariable Long id, @RequestBody Carrito carrito) {
        Carrito existente = carritoService.findById(id);
        if (existente != null) {
            carrito.setId(id);
            return ResponseEntity.ok(carritoService.save(carrito));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
