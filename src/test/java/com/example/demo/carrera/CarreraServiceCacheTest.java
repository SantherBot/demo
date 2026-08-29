package com.example.demo.carrera;

import com.example.demo.common.EstadoRegistro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(CarreraServiceCacheTest.CacheTestConfiguration.class)
class CarreraServiceCacheTest {

    @Autowired
    private CarreraService service;

    @Autowired
    private CarreraRepository repository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        reset(repository);
        cacheManager.getCache("carreras").clear();
    }

    @Test
    void repeatedReadsQueryActiveCareersOnceAndNeverExposeInactiveOnes() {
        Carrera inactiva = carrera(4, "Medicina", EstadoRegistro.INACTIVO);
        List<Carrera> carreras = List.of(
                carrera(1, "Administración de Empresas"),
                carrera(2, "Arquitectura"),
                carrera(3, "Derecho"));
        when(repository.findByStateOrderByNombreAsc(EstadoRegistro.ACTIVO)).thenReturn(carreras);

        List<Carrera> primeraLectura = service.listar();
        List<Carrera> segundaLectura = service.listar();

        assertTrue(AopUtils.isAopProxy(service));
        assertSame(primeraLectura, segundaLectura);
        assertEquals(List.of("Administración de Empresas", "Arquitectura", "Derecho"),
                segundaLectura.stream().map(Carrera::getNombre).toList());
        assertFalse(segundaLectura.contains(inactiva));
        assertTrue(segundaLectura.stream().allMatch(carrera -> carrera.getState() == EstadoRegistro.ACTIVO));
        verify(repository, times(1)).findByStateOrderByNombreAsc(EstadoRegistro.ACTIVO);
    }

    private static Carrera carrera(Integer id, String nombre) {
        Carrera carrera = new Carrera();
        carrera.setId(id);
        carrera.setNombre(nombre);
        return carrera;
    }

    private static Carrera carrera(Integer id, String nombre, EstadoRegistro state) {
        Carrera carrera = carrera(id, nombre);
        carrera.setState(state);
        return carrera;
    }

    @Configuration(proxyBeanMethods = false)
    @EnableCaching
    static class CacheTestConfiguration {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("carreras");
        }

        @Bean
        CarreraRepository carreraRepository() {
            return mock(CarreraRepository.class);
        }

        @Bean
        CarreraService carreraService(CarreraRepository repository) {
            return new CarreraService(repository);
        }
    }
}
