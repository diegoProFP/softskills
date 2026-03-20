package es.ggm.infor.softskills.service.strategy;

import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkillTotalizable;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PenalizacionPorTramosStrategy implements SoftSkillTotalStrategy {

    private static final BigDecimal PUNTUACION_INICIAL = new BigDecimal("10.00");
    private static final BigDecimal PUNTUACION_MINIMA = BigDecimal.ZERO;
    private static final BigDecimal PUNTUACION_MAXIMA = new BigDecimal("10.00");

    @Override
    public TipoMedicionSoftSkill getTipoMedicion() {
        return TipoMedicionSoftSkill.PENALIZACION_POR_TRAMOS;
    }

    @Override
    public void aplicarAlta(SoftSkillTotalizable total, MuestraSoftSkill muestra) {
        BigDecimal puntuacionActual = total.getPuntuacionTotal() != null
                ? total.getPuntuacionTotal()
                : PUNTUACION_INICIAL;

        long muestrasActuales = total.getNumMuestras() != null
                ? total.getNumMuestras()
                : 0L;

        long nuevasMuestras = muestrasActuales + 1;
        BigDecimal ajuste = obtenerAjuste(nuevasMuestras);

        BigDecimal nuevaPuntuacion = muestra.getValor() >= 0
                ? puntuacionActual.add(ajuste)
                : puntuacionActual.subtract(ajuste);
        if (nuevaPuntuacion.compareTo(PUNTUACION_MINIMA) < 0) {
            nuevaPuntuacion = PUNTUACION_MINIMA;
        }
        if (nuevaPuntuacion.compareTo(PUNTUACION_MAXIMA) > 0) {
            nuevaPuntuacion = PUNTUACION_MAXIMA;
        }

        total.setNumMuestras(nuevasMuestras);
        if (muestra.getValor() < 0) {
            long incidenciasActuales = total.getNumIncidencias() != null
                    ? total.getNumIncidencias()
                    : 0L;
            total.setNumIncidencias(incidenciasActuales + 1);
        }
        total.setPuntuacionTotal(nuevaPuntuacion);
    }

    private BigDecimal obtenerAjuste(long numMuestras) {
        if (numMuestras <= 3) {
            return new BigDecimal("0.25");
        }
        if (numMuestras <= 6) {
            return new BigDecimal("0.50");
        }
        if (numMuestras <= 10) {
            return new BigDecimal("0.75");
        }
        return new BigDecimal("1.00");
    }
}
