ALTER TABLE academico.carrera
    ADD COLUMN IF NOT EXISTS state VARCHAR(9);

UPDATE academico.carrera
SET state = 'ACTIVO'
WHERE state IS NULL;

ALTER TABLE academico.carrera
    ALTER COLUMN state SET DEFAULT 'ACTIVO',
    ALTER COLUMN state SET NOT NULL;

ALTER TABLE academico.carrera
    DROP CONSTRAINT IF EXISTS carrera_state_check;

ALTER TABLE academico.carrera
    ADD CONSTRAINT carrera_state_check
    CHECK (state IN ('ACTIVO', 'INACTIVO', 'ELIMINADO'));
