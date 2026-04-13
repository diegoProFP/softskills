package es.ggm.infor.softskills.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AlumnoConTotalesDTO {
    private Long id;
    private String nombre;
    private List<SoftSkillTotalDTO> totalesPorSkill = new ArrayList<>();
    private BigDecimal rankingScore;
    private Integer posicionRanking;
    private Long numMuestrasTotales;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<SoftSkillTotalDTO> getTotalesPorSkill() {
        return totalesPorSkill;
    }

    public void setTotalesPorSkill(List<SoftSkillTotalDTO> totalesPorSkill) {
        this.totalesPorSkill = totalesPorSkill;
    }

    public BigDecimal getRankingScore() {
        return rankingScore;
    }

    public void setRankingScore(BigDecimal rankingScore) {
        this.rankingScore = rankingScore;
    }

    public Integer getPosicionRanking() {
        return posicionRanking;
    }

    public void setPosicionRanking(Integer posicionRanking) {
        this.posicionRanking = posicionRanking;
    }

    public Long getNumMuestrasTotales() {
        return numMuestrasTotales;
    }

    public void setNumMuestrasTotales(Long numMuestrasTotales) {
        this.numMuestrasTotales = numMuestrasTotales;
    }

    @Override
    public String toString() {
        return "AlumnoConTotalesDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", totalesPorSkill=" + totalesPorSkill +
                ", rankingScore=" + rankingScore +
                ", posicionRanking=" + posicionRanking +
                ", numMuestrasTotales=" + numMuestrasTotales +
                '}';
    }
}
