package es.ggm.infor.softskills.service.strategy;

import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.NivelMuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkillTotalizable;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estrategia de medicion basada en acumulacion de evidencias positivas con
 * rendimientos decrecientes. La puntuacion parte de 0 y aumenta cuando se
 * registran muestras positivas. Cada evidencia suma una cantidad a la evidencia
 * acumulada y esa evidencia se transforma despues en una puntuacion entre 0 y 10
 * mediante una funcion de saturacion.
 *
 * Esta logica es adecuada para soft skills que no deben darse por supuestas y
 * que representan aportaciones visibles por encima del minimo esperado. Encaja
 * bien con participacion, iniciativa, proactividad, ayuda al grupo, propuestas
 * de mejora o contribuciones que aportan valor adicional. Un alumno que no
 * genera evidencias en estas competencias no queda penalizado por una incidencia
 * concreta, simplemente no acumula meritos en esa dimension.
 *
 * Las muestras positivas incrementan la evidencia acumulada usando el peso del
 * nivel de la muestra. Si la muestra trae un peso explicito, se respeta ese peso;
 * si no, se usa el peso asociado al nivel: leve, normal o significativa. Las
 * muestras negativas no reducen la evidencia acumulada ni la puntuacion, aunque
 * si incrementan el contador de incidencias. Esto permite conservar informacion
 * sobre observaciones negativas sin convertir esta estrategia en un modelo de
 * penalizacion.
 *
 * La funcion de saturacion evita que la puntuacion crezca de forma lineal e
 * indefinida. Las primeras evidencias tienen un impacto claro, pero cada nueva
 * evidencia aporta proporcionalmente menos cuanto mas alta es la puntuacion. Esto
 * impide que una skill acumulativa se dispare demasiado rapido y refleja mejor
 * la idea pedagogica de progreso: pasar de 0 a 5 requiere pocas evidencias
 * fuertes, pero acercarse a 10 exige consistencia repetida.
 *
 * Resumen: esta estrategia sirve para premiar evidencias positivas acumuladas en
 * competencias de aportacion o iniciativa, haciendo que el progreso sea rapido
 * al principio y mas exigente conforme el alumno se acerca a la excelencia.
 */
@Component
public class AcumulacionSaturadaStrategy implements SoftSkillTotalStrategy {

    private static final BigDecimal PUNTUACION_MINIMA = BigDecimal.ZERO;
    private static final BigDecimal PUNTUACION_MAXIMA = new BigDecimal("10.00");
    private static final BigDecimal FACTOR_SATURACION = new BigDecimal("2.00");

    @Override
    public TipoMedicionSoftSkill getTipoMedicion() {
        return TipoMedicionSoftSkill.ACUMULACION_SATURADA;
    }

    @Override
    public void aplicarAlta(SoftSkillTotalizable total, MuestraSoftSkill muestra) {
        BigDecimal evidenciaActual = obtenerEvidenciaActual(total);
        long muestrasActuales = total.getNumMuestras() != null
                ? total.getNumMuestras()
                : 0L;

        BigDecimal nuevaEvidencia = evidenciaActual;
        if (muestra.getValor() > 0) {
            nuevaEvidencia = nuevaEvidencia.add(obtenerPeso(muestra));
        }

        total.setNumMuestras(muestrasActuales + 1);
        if (muestra.getValor() < 0) {
            long incidenciasActuales = total.getNumIncidencias() != null
                    ? total.getNumIncidencias()
                    : 0L;
            total.setNumIncidencias(incidenciasActuales + 1);
        }
        total.setEvidenciaAcumulada(nuevaEvidencia);
        total.setPuntuacionTotal(calcularPuntuacion(nuevaEvidencia));
    }

    private BigDecimal obtenerPeso(MuestraSoftSkill muestra) {
        if (muestra.getPesoNivel() != null) {
            return muestra.getPesoNivel();
        }

        NivelMuestraSoftSkill nivel = muestra.getNivel() != null
                ? muestra.getNivel()
                : NivelMuestraSoftSkill.NORMAL;
        return nivel.getPeso();
    }

    private BigDecimal obtenerEvidenciaActual(SoftSkillTotalizable total) {
        if (total.getEvidenciaAcumulada() != null) {
            return total.getEvidenciaAcumulada();
        }

        BigDecimal puntuacion = total.getPuntuacionTotal();
        if (puntuacion == null || puntuacion.compareTo(PUNTUACION_MINIMA) <= 0) {
            return BigDecimal.ZERO;
        }
        if (puntuacion.compareTo(PUNTUACION_MAXIMA) >= 0) {
            return PUNTUACION_MAXIMA;
        }

        return FACTOR_SATURACION.multiply(puntuacion)
                .divide(PUNTUACION_MAXIMA.subtract(puntuacion), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularPuntuacion(BigDecimal evidencia) {
        if (evidencia.compareTo(PUNTUACION_MINIMA) <= 0) {
            return PUNTUACION_MINIMA;
        }

        return PUNTUACION_MAXIMA.multiply(evidencia)
                .divide(evidencia.add(FACTOR_SATURACION), 4, RoundingMode.HALF_UP);
    }
}
