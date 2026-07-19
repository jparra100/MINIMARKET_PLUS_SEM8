package com.minimarket.controller;

import com.minimarket.dto.AgregarCarritoRequest;
import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.security.Principal;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Carrito asociado al cliente autenticado")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @Operation(summary = "Listar carrito", description = "Retorna todos los items del carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operacion realizada con exito"),
        @ApiResponse(responseCode = "500", description = "Error interno no controlado en el servidor")
    })
    @GetMapping
    public List<EntityModel<Carrito>> listarCarrito(Principal principal) {
        return carritoService.findByUsername(principal.getName()).stream()
            .map(carrito -> EntityModel.of(carrito,
                linkTo(CarritoController.class).slash(carrito.getId()).withSelfRel()))
            .collect(Collectors.toList());
    }

    @Operation(summary = "Agregar al carrito", description = "Agrega un producto al carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado satisfactoriamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud mal formada o con errores en los datos"),
        @ApiResponse(responseCode = "500", description = "Error interno no controlado en el servidor")
    })
    @PostMapping
    public ResponseEntity<?> agregarProductoAlCarrito(@Valid @RequestBody AgregarCarritoRequest request,
                                                       Principal principal) {
        try {
            Carrito saved = carritoService.agregar(
                    principal.getName(), request.productoId(), request.cantidad());
            EntityModel<Carrito> model = EntityModel.of(saved,
                    linkTo(CarritoController.class).slash(saved.getId()).withSelfRel(),
                    linkTo(methodOn(CarritoController.class)
                            .listarCarrito(principal)).withRel("carrito"));
            return ResponseEntity.status(HttpStatus.CREATED).body(model);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Quitar un producto del carrito")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long id, Principal principal) {
        try {
            carritoService.eliminar(principal.getName(), id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}
