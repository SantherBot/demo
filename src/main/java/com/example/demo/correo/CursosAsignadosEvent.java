package com.example.demo.correo;

import java.util.List;

public record CursosAsignadosEvent(
        Integer estudianteId,
        String correo,
        String nombreCompleto,
        List<String> cursos) {

    public CursosAsignadosEvent {
        cursos = List.copyOf(cursos);
    }
}
