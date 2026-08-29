package com.example.demo.curso;

import com.example.demo.common.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Integer> {

    @Query("""
            SELECT curso
            FROM Curso curso, Carrera carrera
            WHERE curso.carreraId = carrera.id
              AND curso.state = :courseState
              AND carrera.state = :careerState
            ORDER BY carrera.nombre ASC, curso.nombre ASC, curso.codigo ASC
            """)
    List<Curso> findActiveCatalog(@Param("courseState") EstadoRegistro courseState,
                                  @Param("careerState") EstadoRegistro careerState);

    @Query("""
            SELECT curso
            FROM Curso curso, Carrera carrera
            WHERE curso.carreraId = carrera.id
              AND curso.id IN :courseIds
              AND curso.state = :courseState
              AND carrera.state = :careerState
            ORDER BY carrera.nombre ASC, curso.nombre ASC, curso.codigo ASC
            """)
    List<Curso> findActiveByIdsAndActiveCareers(@Param("courseIds") Collection<Integer> courseIds,
                                                @Param("courseState") EstadoRegistro courseState,
                                                @Param("careerState") EstadoRegistro careerState);
}
