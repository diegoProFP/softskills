package es.ggm.infor.softskills.service.strategy;

import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.NivelMuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkillTotalizable;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
