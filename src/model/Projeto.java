package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Projeto {
    private int id;
    private String nome;
    private String descricao;
    private Date dataInicio;
    private Date dataTerminoPrevista;
    private String status;
    private int gerenteId;
    private List<Equipe> equipesAlocadas;

    public Projeto() {
    }

    public Projeto(String nome, String descricao, Date dataInicio,
            Date dataTerminoPrevista, String status, int gerenteId) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataTerminoPrevista = dataTerminoPrevista;
        this.status = status;
        this.gerenteId = gerenteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTerminoPrevista() {
        return dataTerminoPrevista;
    }

    public void setDataTerminoPrevista(Date dataTerminoPrevista) {
        this.dataTerminoPrevista = dataTerminoPrevista;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getGerenteId() {
        return gerenteId;
    }

    public void setGerenteId(int gerenteId) {
        this.gerenteId = gerenteId;
    }

    public List<Equipe> getEquipesAlocadas() {
        return equipesAlocadas;
    }

    public void setEquipesAlocadas(List<Equipe> equipesAlocadas) {
        this.equipesAlocadas = equipesAlocadas;
    }

    public void adicionarEquipe(Equipe equipe) {
        if (equipesAlocadas == null) {
            equipesAlocadas = new ArrayList<>();
        }
        equipesAlocadas.add(equipe);
    }

    @Override
    public String toString() {
        return nome + " - " + status;
    }

}
