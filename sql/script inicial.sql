-- Script inicial para datos de referencia de Soft Skills.
-- Añade aquí los INSERT de las tablas que aún no tienen sección de administración.

INSERT INTO softskills_db.soft_skill (id,descripcion,nombre,tipo,TIPO_MEDICION,CODIGO,PRIORIDAD_RANKING) VALUES
	 (1,'Permite medir cómo maneja el alumno las distracciones','Enfoque y concentración',0,NULL,'ENFOQUE_DISTRACCIONES',3),
	 (2,'Mide la puntualidad del alumno','Puntualidad',0,NULL,'PUNTUALIDAD',3);
INSERT INTO softskills_db.profesor (ID) VALUES
	 (1445),
	 (1446),
	 (2312),
	 (2927);
INSERT INTO softskills_db.motivo_soft_skill (id,motivo) VALUES
	 (1,'No ha traido el cuaderno');
