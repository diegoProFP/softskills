-- Añade la referencia explicita desde un grupo academico al curso Moodle
-- que representa al grupo completo/tutoria.
--
-- No se valida contra Moodle en base de datos: el ID se configura manualmente
-- desde administracion y debe corresponder a un curso Moodle existente.

ALTER TABLE GRUPO
    ADD COLUMN cursoMoodleGrupoId BIGINT NULL;
