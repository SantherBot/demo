package com.example.demo.curso;

import com.example.demo.common.EstadoRegistro;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CursoService {

    private final CursoRepository repository;

    public CursoService(CursoRepository repository) {
        this.repository = repository;
    }

    @Cacheable("cursosActivos")
    public List<Curso> listar() {
        return repository.findActiveCatalog(EstadoRegistro.ACTIVO, EstadoRegistro.ACTIVO);
    }

    public List<Curso> loadActiveByIds(Collection<Integer> courseIds) {
        if (courseIds.isEmpty()) {
            return List.of();
        }
        return repository.findActiveByIdsAndActiveCareers(
                courseIds, EstadoRegistro.ACTIVO, EstadoRegistro.ACTIVO);
    }
}
