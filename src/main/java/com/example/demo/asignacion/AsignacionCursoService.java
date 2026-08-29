package com.example.demo.asignacion;

import com.example.demo.common.EstadoRegistro;
import com.example.demo.correo.CursosAsignadosEvent;
import com.example.demo.curso.Curso;
import com.example.demo.curso.CursoService;
import com.example.demo.estudiante.Estudiante;
import com.example.demo.estudiante.EstudianteRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AsignacionCursoService {

    private final EstudianteRepository estudianteRepository;
    private final EstudianteCursoRepository assignmentRepository;
    private final CursoService cursoService;
    private final ApplicationEventPublisher eventPublisher;

    public AsignacionCursoService(EstudianteRepository estudianteRepository,
                                  EstudianteCursoRepository assignmentRepository,
                                  CursoService cursoService,
                                  ApplicationEventPublisher eventPublisher) {
        this.estudianteRepository = estudianteRepository;
        this.assignmentRepository = assignmentRepository;
        this.cursoService = cursoService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public List<Curso> assignCourses(Integer estudianteId, AsignacionCursosRequest request) {
        Estudiante estudiante = estudianteRepository.findByIdAndState(estudianteId, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Active student not found: " + estudianteId));

        if (request == null || request.cursoIds() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cursoIds is required");
        }

        List<Integer> courseIds = request.cursoIds();
        if (courseIds.stream().anyMatch(id -> id == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cursoIds cannot contain null values");
        }

        Set<Integer> requestedIds = new HashSet<>(courseIds);
        if (requestedIds.size() != courseIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cursoIds cannot contain duplicates");
        }

        List<Curso> courses = cursoService.loadActiveByIds(courseIds);
        if (courses.size() != courseIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Each course must exist, be active, and belong to an active career");
        }

        Map<Integer, Curso> coursesById = new HashMap<>();
        for (Curso course : courses) {
            coursesById.put(course.getId(), course);
        }

        List<EstudianteCurso> assignments = assignmentRepository.findByEstudianteId(estudianteId);
        Map<Integer, EstudianteCurso> assignmentsByCourse = new HashMap<>();
        OffsetDateTime assignedAt = OffsetDateTime.now();
        for (EstudianteCurso assignment : assignments) {
            assignmentsByCourse.put(assignment.getCursoId(), assignment);
            if (requestedIds.contains(assignment.getCursoId())) {
                assignment.setState(EstadoRegistro.ACTIVO);
                assignment.setAsignadoAt(assignedAt);
            } else if (assignment.getState() == EstadoRegistro.ACTIVO) {
                assignment.setState(EstadoRegistro.INACTIVO);
            }
        }

        for (Integer courseId : courseIds) {
            if (!assignmentsByCourse.containsKey(courseId)) {
                EstudianteCurso assignment = new EstudianteCurso();
                assignment.setEstudianteId(estudianteId);
                assignment.setCursoId(courseId);
                assignment.setState(EstadoRegistro.ACTIVO);
                assignment.setAsignadoAt(assignedAt);
                assignments.add(assignment);
            }
        }
        assignmentRepository.saveAll(assignments);

        List<String> courseLines = new ArrayList<>();
        for (Integer courseId : courseIds) {
            Curso course = coursesById.get(courseId);
            courseLines.add(course.getCodigo() + " - " + course.getNombre());
        }
        eventPublisher.publishEvent(new CursosAsignadosEvent(
                estudiante.getId(), estudiante.getCorreo(), estudiante.getNombreCompleto(), courseLines));

        return courses;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "cursosAsignadosPorEstudiante", key = "#estudianteId")
    public List<Curso> listAssignedCourses(Integer estudianteId) {
        List<Integer> courseIds = assignmentRepository
                .findByEstudianteIdAndState(estudianteId, EstadoRegistro.ACTIVO)
                .stream()
                .map(EstudianteCurso::getCursoId)
                .toList();
        return cursoService.loadActiveByIds(courseIds);
    }
}
