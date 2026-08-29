package com.example.demo.carrera;

import com.example.demo.common.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarreraRepository extends JpaRepository<Carrera, Integer> {

    List<Carrera> findByStateOrderByNombreAsc(EstadoRegistro state);
}
