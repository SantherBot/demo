package com.example.demo.estudiante;

import com.example.demo.common.EstadoRegistro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(EstudianteServiceCacheTest.CacheTestConfiguration.class)
class EstudianteServiceCacheTest {

    @Autowired
    private EstudianteService service;

    @Autowired
    private EstudianteRepository repository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        reset(repository);
        cacheManager.getCache("estudiantesVigentes").clear();
        cacheManager.getCache("estudiantesPorId").clear();
    }

    @Test
    void repeatedReadsUseRepositoryOnce() {
        Estudiante estudiante = estudiante(1, "Ana");
        when(repository.findByStateNot(EstadoRegistro.ELIMINADO)).thenReturn(List.of(estudiante));
        when(repository.findByIdAndStateNot(1, EstadoRegistro.ELIMINADO))
                .thenReturn(Optional.of(estudiante));

        assertSame(service.listar(), service.listar());
        assertSame(service.buscarPorId(1), service.buscarPorId(1));

        verify(repository, times(1)).findByStateNot(EstadoRegistro.ELIMINADO);
        verify(repository, times(1)).findByIdAndStateNot(1, EstadoRegistro.ELIMINADO);
    }

    @Test
    void createCachesCreatedStudentAndInvalidatesList() {
        Estudiante existente = estudiante(1, "Ana");
        Estudiante nuevo = estudiante(null, "Beto");
        when(repository.findByStateNot(EstadoRegistro.ELIMINADO))
                .thenReturn(List.of(existente), List.of(existente, nuevo));
        when(repository.save(nuevo)).thenAnswer(invocation -> {
            nuevo.setId(2);
            return nuevo;
        });

        service.listar();
        Estudiante creado = service.crear(nuevo);

        assertSame(creado, service.buscarPorId(2));
        assertEquals(2, service.listar().size());
        verify(repository, never()).findByIdAndStateNot(2, EstadoRegistro.ELIMINADO);
        verify(repository, times(2)).findByStateNot(EstadoRegistro.ELIMINADO);
    }

    @Test
    void updateRefreshesStudentAndInvalidatesList() {
        Estudiante cacheado = estudiante(1, "Ana");
        Estudiante persistido = estudiante(1, "Ana");
        Estudiante datos = estudiante(null, "Ana Maria");
        when(repository.findByStateNot(EstadoRegistro.ELIMINADO))
                .thenReturn(List.of(cacheado), List.of(persistido));
        when(repository.findByIdAndStateNot(1, EstadoRegistro.ELIMINADO))
                .thenReturn(Optional.of(cacheado), Optional.of(persistido));
        when(repository.save(any(Estudiante.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.listar();
        service.buscarPorId(1);
        Estudiante actualizado = service.actualizar(1, datos);

        assertSame(actualizado, service.buscarPorId(1));
        assertEquals("Ana Maria", service.buscarPorId(1).getNombreCompleto());
        service.listar();
        verify(repository, times(2)).findByIdAndStateNot(1, EstadoRegistro.ELIMINADO);
        verify(repository, times(2)).findByStateNot(EstadoRegistro.ELIMINADO);
    }

    @Test
    void successfulDeleteEvictsStudentAndList() {
        Estudiante cacheado = estudiante(1, "Ana");
        Estudiante paraEliminar = estudiante(1, "Ana");
        when(repository.findByStateNot(EstadoRegistro.ELIMINADO))
                .thenReturn(List.of(cacheado), List.of());
        when(repository.findByIdAndStateNot(1, EstadoRegistro.ELIMINADO))
                .thenReturn(Optional.of(cacheado), Optional.of(paraEliminar), Optional.empty());
        when(repository.save(paraEliminar)).thenReturn(paraEliminar);

        service.listar();
        service.buscarPorId(1);
        service.eliminar(1);

        assertThrows(ResponseStatusException.class, () -> service.buscarPorId(1));
        assertEquals(0, service.listar().size());
        verify(repository, times(3)).findByIdAndStateNot(1, EstadoRegistro.ELIMINADO);
        verify(repository, times(2)).findByStateNot(EstadoRegistro.ELIMINADO);
    }

    @Test
    void failedDeleteKeepsCachedEntries() {
        Estudiante cacheado = estudiante(1, "Ana");
        Estudiante paraEliminar = estudiante(1, "Ana");
        when(repository.findByStateNot(EstadoRegistro.ELIMINADO)).thenReturn(List.of(cacheado));
        when(repository.findByIdAndStateNot(1, EstadoRegistro.ELIMINADO))
                .thenReturn(Optional.of(cacheado), Optional.of(paraEliminar));
        doThrow(new IllegalStateException("save failed")).when(repository).save(paraEliminar);

        List<Estudiante> listaCacheada = service.listar();
        Estudiante estudianteCacheado = service.buscarPorId(1);

        assertThrows(IllegalStateException.class, () -> service.eliminar(1));
        assertSame(estudianteCacheado, service.buscarPorId(1));
        assertSame(listaCacheada, service.listar());
        verify(repository, times(2)).findByIdAndStateNot(1, EstadoRegistro.ELIMINADO);
        verify(repository, times(1)).findByStateNot(EstadoRegistro.ELIMINADO);
    }

    private static Estudiante estudiante(Integer id, String nombre) {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(id);
        estudiante.setCarnet("C-" + (id == null ? "nuevo" : id));
        estudiante.setNombreCompleto(nombre);
        estudiante.setCorreo(nombre.toLowerCase().replace(' ', '.') + "@example.com");
        estudiante.setTelefono("12345678");
        estudiante.setCarrera("Ingenieria");
        return estudiante;
    }

    @Configuration(proxyBeanMethods = false)
    @EnableCaching
    static class CacheTestConfiguration {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("estudiantesVigentes", "estudiantesPorId");
        }

        @Bean
        EstudianteRepository estudianteRepository() {
            return mock(EstudianteRepository.class);
        }

        @Bean
        EstudianteService estudianteService(EstudianteRepository repository) {
            return new EstudianteService(repository);
        }
    }
}
