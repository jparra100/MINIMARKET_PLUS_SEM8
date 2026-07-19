package com.minimarket.controller;

import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Administracion protegida de clientes y empleados")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar usuarios", description = "Retorna la lista completa de usuarios")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operacion realizada con exito"),
        @ApiResponse(responseCode = "500", description = "Error interno no controlado en el servidor")
    })
    @GetMapping
    public List<EntityModel<Usuario>> listarUsuarios() {
        return usuarioService.findAll().stream()
            .map(usuario -> EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel()))
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario")
    public EntityModel<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return EntityModel.of(usuario.orElseThrow(),
            linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(id)).withSelfRel(),
            linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("lista-usuarios"));
    }

    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado satisfactoriamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud mal formada o con errores en los datos"),
        @ApiResponse(responseCode = "500", description = "Error interno no controlado en el servidor")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> guardarUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario saved = usuarioService.save(usuario);
        EntityModel<Usuario> model = EntityModel.of(saved,
            linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(saved.getId())).withSelfRel(),
            linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("lista-usuarios"));
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id,
                                                       @Valid @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            usuario.setId(id);
            return ResponseEntity.ok(usuarioService.save(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
