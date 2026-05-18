-- Permite guardar el nuevo tipo de medicion EVIDENCIA_MIXTA en bases existentes.
-- El error habitual antes de aplicar esta migracion es:
-- Data truncated for column 'TIPO_MEDICION'
--
-- Causa:
-- En MySQL, Hibernate puede haber creado TIPO_MEDICION como ENUM con los valores
-- antiguos. Al insertar AUTONOMIA con EVIDENCIA_MIXTA, MySQL rechaza el valor.

ALTER TABLE SOFT_SKILL
    MODIFY TIPO_MEDICION VARCHAR(50) NULL;
