package com.example.demo.estudiante;

import com.example.demo.common.EstadoRegistro;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Logica del CRUD de Estudiante.
 * El Controller llama a estos metodos; ellos usan el Repository.
 */
@Service
public class EstudianteService {

    private final EstudianteRepository repository;

    // Spring "inyecta" el repositorio por el constructor.
    public EstudianteService(EstudianteRepository repository) {
        this.repository = repository;
    }

    // ---- READ: listar todos los vigentes (sin eliminados) ----
    @Cacheable("estudiantesVigentes")
    public List<Estudiante> listar() {
        return repository.findByStateNot(EstadoRegistro.ELIMINADO);
    }

    // ---- READ: buscar uno por id (si no existe o esta eliminado -> 404) ----
    @Cacheable(cacheNames = "estudiantesPorId", key = "#id")
    public Estudiante buscarPorId(Integer id) {
        return repository.findByIdAndStateNot(id, EstadoRegistro.ELIMINADO)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudiante no encontrado: " + id));
    }

    // ---- CREATE ----
    @Transactional
    @Caching(
            put = @CachePut(cacheNames = "estudiantesPorId", key = "#result.id"),
            evict = @CacheEvict(cacheNames = "estudiantesVigentes", allEntries = true))
    public Estudiante crear(Estudiante estudiante) {
        estudiante.setId(null);
        estudiante.setState(EstadoRegistro.ACTIVO);
        return repository.save(estudiante);
    }

    // ---- UPDATE ----
    @Transactional
    @Caching(
            put = @CachePut(cacheNames = "estudiantesPorId", key = "#id"),
            evict = @CacheEvict(cacheNames = "estudiantesVigentes", allEntries = true))
    public Estudiante actualizar(Integer id, Estudiante datos) {
        Estudiante actual = buscarPorId(id);          // valida que exista y no este eliminado
        actual.setCarnet(datos.getCarnet());
        actual.setNombreCompleto(datos.getNombreCompleto());
        actual.setCorreo(datos.getCorreo());
        actual.setTelefono(datos.getTelefono());
        actual.setCarrera(datos.getCarrera());
        return repository.save(actual);
    }

    // ---- DELETE: no borra, solo marca ELIMINADO ----
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "estudiantesPorId", key = "#id"),
            @CacheEvict(cacheNames = "estudiantesVigentes", allEntries = true)
    })
    public void eliminar(Integer id) {
        Estudiante actual = buscarPorId(id);
        actual.setState(EstadoRegistro.ELIMINADO);
        repository.save(actual);
    }
}
