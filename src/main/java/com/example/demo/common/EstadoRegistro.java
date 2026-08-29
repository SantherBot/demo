package com.example.demo.common;

/**
 * Estado del registro para la baja logica (soft delete).
 * Se persiste como texto en la columna state varchar(9)
 * gracias a @Enumerated(EnumType.STRING) en BaseEntity.
 */
public enum EstadoRegistro {
    ACTIVO,
    INACTIVO,
    ELIMINADO
}
