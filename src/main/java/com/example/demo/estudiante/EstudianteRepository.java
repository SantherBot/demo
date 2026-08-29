package com.example.demo.estudiante;

import com.example.demo.common.EstadoRegistro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Estudiante.
 * JpaRepository ya trae: save(), findById(), findAll(), etc.
 * Aqui agregamos consultas que EXCLUYEN los eliminados (baja logica).
 */
public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {

    /** Lista todos los estudiantes cuyo state NO sea el que pasemos
     *  (le pasaremos ELIMINADO para traer solo los vigentes). */
    List<Estudiante> findByStateNot(EstadoRegistro state);

    /** Busca un estudiante por id, siempre que NO este eliminado. */
    Optional<Estudiante> findByIdAndStateNot(Integer id, EstadoRegistro state);

    /** Busca un estudiante por id con el estado exacto requerido por una asignacion. */
    Optional<Estudiante> findByIdAndState(Integer id, EstadoRegistro state);
}
