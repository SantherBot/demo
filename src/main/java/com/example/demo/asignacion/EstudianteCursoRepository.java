package com.example.demo.asignacion;

import com.example.demo.common.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstudianteCursoRepository extends JpaRepository<EstudianteCurso, Integer> {

    List<EstudianteCurso> findByEstudianteId(Integer estudianteId);

    List<EstudianteCurso> findByEstudianteIdAndState(Integer estudianteId, EstadoRegistro state);
}
