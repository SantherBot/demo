package com.example.demo.common;

import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;

import java.time.OffsetDateTime;

/**
 * Superclase mapeada con los campos comunes a todas las entidades
 * del arquetipo: estado (baja logica), version optimista y auditoria.
 * NO incluye el @Id: cada entidad declara su propia PK con su nombre.
 */
@MappedSuperclass
public abstract class BaseEntity {

    /** Baja logica: ACTIVO / INACTIVO / ELIMINADO (columna state varchar(9)). */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 9)
    private EstadoRegistro state = EstadoRegistro.ACTIVO;

    /** Bloqueo optimista (row_version). Hibernate lo maneja con @Version. */
    @Version
    @Column(name = "row_version", nullable = false)
    private Integer rowVersion = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    // ---- Callbacks de auditoria ----

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (state == null) {
            state = EstadoRegistro.ACTIVO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // ---- Getters / Setters ----

    public EstadoRegistro getState() {
        return state;
    }

    public void setState(EstadoRegistro state) {
        this.state = state;
    }

    public Integer getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(Integer rowVersion) {
        this.rowVersion = rowVersion;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }
}
