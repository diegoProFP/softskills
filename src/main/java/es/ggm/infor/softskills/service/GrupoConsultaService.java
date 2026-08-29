package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.GrupoRepository;
import es.ggm.infor.softskills.dto.AdminGrupoAcademicoRequest;
import es.ggm.infor.softskills.model.Grupo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrupoConsultaService {

    private final GrupoRepository grupoRepository;

    public List<Grupo> getAllGrupos() {
        return grupoRepository.findAll();
    }

    @Transactional
    public Grupo actualizarCursoMoodleGrupo(Long grupoId, AdminGrupoAcademicoRequest request) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo academico no encontrado"));

        grupo.setCursoMoodleGrupoId(request.getCursoMoodleGrupoId());
        return grupoRepository.save(grupo);
    }
}
