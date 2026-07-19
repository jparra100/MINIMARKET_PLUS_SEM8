package com.minimarket.controller;

import com.minimarket.entity.Categoria;
import com.minimarket.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Categorias del catalogo de productos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar categorias")
    public List<EntityModel<Categoria>> listarCategorias() {
        return categoriaService.findAll().stream().map(this::modelo).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una categoria")
    public ResponseEntity<EntityModel<Categoria>> obtenerCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        return (categoria != null) ? ResponseEntity.ok(modelo(categoria)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Crear una categoria")
    public ResponseEntity<EntityModel<Categoria>> guardarCategoria(@Valid @RequestBody Categoria categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(modelo(categoriaService.save(categoria)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una categoria")
    public ResponseEntity<EntityModel<Categoria>> actualizarCategoria(@PathVariable Long id,
                                                                       @Valid @RequestBody Categoria categoria) {
        Categoria categoriaExistente = categoriaService.findById(id);
        if (categoriaExistente != null) {
            categoria.setId(id);
            return ResponseEntity.ok(modelo(categoriaService.save(categoria)));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una categoria")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria != null) {
            categoriaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private EntityModel<Categoria> modelo(Categoria categoria) {
        return EntityModel.of(categoria,
                linkTo(methodOn(CategoriaController.class)
                        .obtenerCategoriaPorId(categoria.getId())).withSelfRel(),
                linkTo(methodOn(CategoriaController.class)
                        .listarCategorias()).withRel("categorias"),
                linkTo(methodOn(ProductoController.class)
                        .listarProductos()).withRel("productos"));
    }
}
