package com.example.productomain.controller;

import com.example.productomain.model.Producto;
import com.example.productomain.service.ProductoService;
import com.example.productomain.service.SqsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private SqsService sqsService;

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto,
                                                   Authentication authentication) {
        Producto nuevo = productoService.save(producto);
        String usuario = authentication != null ? authentication.getName() : "anonimo";
        sqsService.enviarEvento("CREAR", nuevo.getId(), nuevo.getNombre(), usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id,
                                                       @RequestBody Producto producto,
                                                       Authentication authentication) {
        if (productoService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        producto.setId(id);
        Producto actualizado = productoService.save(producto);
        String usuario = authentication != null ? authentication.getName() : "anonimo";
        sqsService.enviarEvento("MODIFICAR", actualizado.getId(), actualizado.getNombre(), usuario);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id,
                                                  Authentication authentication) {
        return productoService.findById(id).map(p -> {
            productoService.eliminarLogico(id);
            String usuario = authentication != null ? authentication.getName() : "anonimo";
            sqsService.enviarEvento("ELIMINAR", id, p.getNombre(), usuario);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
