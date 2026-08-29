package com.example.demo.estudiante;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST del CRUD de Estudiante.
 * Recibe las peticiones HTTP y delega en el Service.
 * Ruta base: /api/estudiantes
 */
@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService service;

    public EstudianteController(EstudianteService service) {
        this.service = service;
    }

    // READ - listar todos (GET /api/estudiantes)
    @GetMapping
    public List<Estudiante> listar() {
        return service.listar();
    }

    // READ - uno por id (GET /api/estudiantes/1)
    @GetMapping("/{id}")
    public Estudiante buscar(@PathVariable Integer id) {
        return service.buscarPorId(id);
    }

    // CREATE (POST /api/estudiantes) -> devuelve 201 Created
    @PostMapping
    public ResponseEntity<Estudiante> crear(@RequestBody Estudiante estudiante) {
        Estudiante creado = service.crear(estudiante);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // UPDATE (PUT /api/estudiantes/1)
    @PutMapping("/{id}")
    public Estudiante actualizar(@PathVariable Integer id,
                                 @RequestBody Estudiante datos) {
        return service.actualizar(id, datos);
    }

    // DELETE logico (DELETE /api/estudiantes/1) -> devuelve 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}