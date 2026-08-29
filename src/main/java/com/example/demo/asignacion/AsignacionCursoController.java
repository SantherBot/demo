package com.example.demo.asignacion;

import com.example.demo.curso.Curso;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes/{estudianteId}/cursos")
public class AsignacionCursoController {

    private final AsignacionCursoService service;

    public AsignacionCursoController(AsignacionCursoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Curso> list(@PathVariable Integer estudianteId) {
        return service.listAssignedCourses(estudianteId);
    }

    @PutMapping
    public List<Curso> assign(@PathVariable Integer estudianteId,
                              @RequestBody AsignacionCursosRequest request) {
        return service.assignCourses(estudianteId, request);
    }
}
