package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.GrupoRepository;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.Grupo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GrupoService {

    private final GrupoRepository grupoRepository;

    @Transactional
    public Grupo resolverGrupoDesdeCurso(Curso curso) {
        if (curso == null) {
            return null;
        }

        if (curso.getGrupoAcademico() != null) {
            return curso.getGrupoAcademico();
        }

        GrupoDatos grupoDatos = extraerGrupoDatos(curso.getIdNumber());
        if (grupoDatos == null) {
            return null;
        }

        Grupo grupo = grupoRepository.findByNivelAndCicloFormativoAndGrupoAndCursoEscolar(
                grupoDatos.nivel(), grupoDatos.cicloFormativo(), grupoDatos.grupo(), grupoDatos.cursoEscolar()
        ).orElseGet(() -> grupoRepository.save(Grupo.builder()
                .nivel(grupoDatos.nivel())
                .cicloFormativo(grupoDatos.cicloFormativo())
                .grupo(grupoDatos.grupo())
                .cursoEscolar(grupoDatos.cursoEscolar())
                .build()));

        curso.setGrupoAcademico(grupo);
        return grupo;
    }

    private GrupoDatos extraerGrupoDatos(String idNumber) {
        if (idNumber == null || idNumber.isBlank()) {
            return null;
        }

        String[] partes = idNumber.split("_");
        if (partes.length != 4) {
            return null;
        }

        return new GrupoDatos(
                partes[0],
                partes[1],
                partes[2],
                normalizarCursoEscolar(partes[3])
        );
    }

    private String normalizarCursoEscolar(String cursoEscolarRaw) {
        if (cursoEscolarRaw == null || cursoEscolarRaw.isBlank()) {
            return cursoEscolarRaw;
        }

        if (cursoEscolarRaw.length() == 4) {
            return cursoEscolarRaw.substring(0, 2) + "-" + cursoEscolarRaw.substring(2);
        }

        return cursoEscolarRaw;
    }

    private record GrupoDatos(String nivel, String cicloFormativo, String grupo, String cursoEscolar) {
    }
}
