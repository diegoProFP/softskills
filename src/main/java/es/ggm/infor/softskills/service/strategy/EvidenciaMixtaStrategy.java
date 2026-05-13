package es.ggm.infor.softskills.service.strategy;

import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.NivelMuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkillTotalizable;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Estrategia de medicion pensada para soft skills que no deben presumirse como
 * completamente dominadas por defecto, pero que tampoco seria justo iniciar en
 * cero por ausencia de evidencias. La puntuacion inicial es una zona neutra
 * intermedia, actualmente 5 sobre 10, y cada muestra observada desplaza esa
 * puntuacion hacia arriba o hacia abajo segun el signo de la evidencia.
 *
 * Esta estrategia encaja con competencias como autonomia, responsabilidad,
 * resolucion de problemas o comunicacion profesional, donde el profesor necesita
 * reflejar tanto conductas positivas como incidencias negativas. Por ejemplo,
 * en autonomia, no preguntar nunca no equivale necesariamente a tener un 10:
 * puede ser autonomia real si el alumno avanza, o bloqueo pasivo si no progresa
 * ni comunica dificultades. Por eso la estrategia parte de una base neutra y
 * exige evidencias para justificar movimientos en la nota.
 *
 * Las muestras positivas incrementan la puntuacion y las negativas la reducen.
 * El nivel de la muestra determina la magnitud del ajuste. Las evidencias
 * positivas pesan ligeramente menos que las negativas equivalentes: una mejora
 * leve suma 0.40, normal suma 0.80 y significativa suma 1.50; una incidencia
 * leve resta 0.50, normal resta 1.00 y significativa resta 2.00. Esta asimetria
 * es intencionada: en competencias profesionales, una conducta problematica
 * clara, como dependencia reiterada o bloqueo pasivo prolongado, debe tener
 * impacto suficiente y no quedar neutralizada facilmente por varias evidencias
 * menores.
 *
 * La estrategia mantiene la puntuacion acotada entre 0 y 10, incrementa siempre
 * el numero total de muestras y solo incrementa el contador de incidencias
 * cuando la muestra es negativa. No usa evidencia acumulada porque su modelo no
 * es de crecimiento progresivo saturado, sino de ajuste alrededor de una base
 * pedagogicamente neutra.
 *
 * Resumen: esta estrategia sirve para competencias que deben demostrarse con
 * evidencias observables, permitiendo premiar el buen desempeno y penalizar
 * incidencias relevantes sin regalar una nota alta por silencio o falta de
 * registros.
 */
@Component
public class EvidenciaMixtaStrategy implements SoftSkillTotalStrategy {

    private static final BigDecimal PUNTUACION_INICIAL = new BigDecimal("5.00");
    private static final BigDecimal PUNTUACION_MINIMA = BigDecimal.ZERO;
    private static final BigDecimal PUNTUACION_MAXIMA = new BigDecimal("10.00");

    @Override
    public TipoMedicionSoftSkill getTipoMedicion() {
        return TipoMedicionSoftSkill.EVIDENCIA_MIXTA;
    }

    @Override
    public void aplicarAlta(SoftSkillTotalizable total, MuestraSoftSkill muestra) {
        BigDecimal puntuacionActual = total.getPuntuacionTotal() != null
                ? total.getPuntuacionTotal()
                : PUNTUACION_INICIAL;

        long muestrasActuales = total.getNumMuestras() != null
                ? total.getNumMuestras()
                : 0L;

        BigDecimal ajuste = obtenerAjuste(muestra);
        BigDecimal nuevaPuntuacion = muestra.getValor() >= 0
                ? puntuacionActual.add(ajuste)
                : puntuacionActual.subtract(ajuste);

        total.setNumMuestras(muestrasActuales + 1);
        if (muestra.getValor() < 0) {
            long incidenciasActuales = total.getNumIncidencias() != null
                    ? total.getNumIncidencias()
                    : 0L;
            total.setNumIncidencias(incidenciasActuales + 1);
        }
        total.setPuntuacionTotal(acotar(nuevaPuntuacion));
    }

    private BigDecimal obtenerAjuste(MuestraSoftSkill muestra) {
        NivelMuestraSoftSkill nivel = muestra.getNivel() != null
                ? muestra.getNivel()
                : NivelMuestraSoftSkill.NORMAL;

        boolean positiva = muestra.getValor() >= 0;
        return switch (nivel) {
            case LEVE -> positiva ? new BigDecimal("0.40") : new BigDecimal("0.50");
            case NORMAL -> positiva ? new BigDecimal("0.80") : new BigDecimal("1.00");
            case SIGNIFICATIVA -> positiva ? new BigDecimal("1.50") : new BigDecimal("2.00");
        };
    }

    private BigDecimal acotar(BigDecimal puntuacion) {
        if (puntuacion.compareTo(PUNTUACION_MINIMA) < 0) {
            return PUNTUACION_MINIMA;
        }
        if (puntuacion.compareTo(PUNTUACION_MAXIMA) > 0) {
            return PUNTUACION_MAXIMA;
        }
        return puntuacion;
    }
}
