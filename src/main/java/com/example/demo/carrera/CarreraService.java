package com.example.demo.carrera;

import com.example.demo.common.EstadoRegistro;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CarreraService {

    private final CarreraRepository repository;

    public CarreraService(CarreraRepository repository) {
        this.repository = repository;
    }

    @Cacheable("carreras")
    public List<Carrera> listar() {
        return repository.findByStateOrderByNombreAsc(EstadoRegistro.ACTIVO);
    }
}
