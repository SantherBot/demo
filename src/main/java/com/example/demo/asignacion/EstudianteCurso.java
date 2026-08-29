package com.example.demo.asignacion;

import com.example.demo.common.EstadoRegistro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "estudiante_curso", schema = "academico")
public class EstudianteCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estudiante_curso_id")
    private Integer id;

    @Column(name = "estudiante_id", nullable = false)
    private Integer estudianteId;

    @Column(name = "curso_id", nullable = false)
    private Integer cursoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 9)
    private EstadoRegistro state = EstadoRegistro.ACTIVO;

    @Column(name = "asignado_at", nullable = false)
    private OffsetDateTime asignadoAt = OffsetDateTime.now();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Integer estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Integer getCursoId() {
        return cursoId;
    }

    public void setCursoId(Integer cursoId) {
        this.cursoId = cursoId;
    }

    public EstadoRegistro getState() {
        return state;
    }

    public void setState(EstadoRegistro state) {
        this.state = state;
    }

    public OffsetDateTime getAsignadoAt() {
        return asignadoAt;
    }

    public void setAsignadoAt(OffsetDateTime asignadoAt) {
        this.asignadoAt = asignadoAt;
    }
}
