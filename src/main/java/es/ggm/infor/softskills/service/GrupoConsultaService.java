package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.GrupoRepository;
import es.ggm.infor.softskills.model.Grupo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrupoConsultaService {

    private final GrupoRepository grupoRepository;

    public List<Grupo> getAllGrupos() {
        return grupoRepository.findAll();
    }
}
