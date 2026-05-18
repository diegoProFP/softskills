package es.ggm.infor.softskills.service.strategy;

import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkillTotalizable;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Estrategia de medicion basada en una expectativa inicial de cumplimiento
 * completo. La puntuacion parte de 10 sobre 10 y se va ajustando cuando se
 * registran muestras positivas o negativas. Esta logica es adecuada para soft
 * skills que representan normas basicas de funcionamiento en el aula o en un
 * entorno profesional, donde el alumno se considera correcto mientras no existan
 * incidencias observables.
 *
 * Encaja especialmente con competencias de mantenimiento de conducta, como
 * puntualidad, respeto de normas, asistencia activa o enfoque ante
 * distracciones. En estos casos, la ausencia de incidencias si puede interpretarse
 * razonablemente como buen desempeno: si un alumno llega a tiempo, no interrumpe,
 * no usa el movil indebidamente y mantiene la concentracion, no hace falta que
 * genere evidencias extraordinarias para conservar una puntuacion alta.
 *
 * El ajuste se calcula por tramos en funcion del numero total de muestras
 * registradas para esa skill y alumno. Las primeras muestras tienen un impacto
 * pequeno, porque una observacion aislada no deberia alterar demasiado la nota.
 * A medida que se acumulan registros, el impacto por muestra aumenta: hasta tres
 * muestras se ajusta 0.25, entre cuatro y seis se ajusta 0.50, entre siete y diez
 * se ajusta 0.75 y a partir de ahi se ajusta 1.00. Este comportamiento hace que
 * un patron repetido tenga mas peso que una incidencia puntual.
 *
 * Las muestras positivas pueden recuperar puntuacion hasta el maximo de 10 y las
 * negativas pueden reducirla hasta el minimo de 0. La estrategia incrementa
 * siempre el numero de muestras y solo incrementa el contador de incidencias
 * cuando el valor registrado es negativo. No distingue por nivel de muestra, ya
 * que el peso viene dado por la persistencia del patron y no por una graduacion
 * explicita de severidad.
 *
 * Resumen: esta estrategia sirve para conductas esperadas por defecto, donde se
 * parte de una puntuacion alta y se penalizan patrones de incumplimiento, con
 * posibilidad de recuperacion si aparecen evidencias positivas posteriores.
 */
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
