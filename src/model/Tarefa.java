package model;

import java.util.Date;

public class Tarefa {
    private int id;
    private String titulo;
    private String descricao;
    private int projetoId;
    private int responsavelId;
    private String status;
    private Date dataInicio;
    private Date dataFimPrevista;
    private Date dataFimReal;

    public Tarefa() {
    }

    public Tarefa(String titulo, String descricao, int projetoId, int responsavelId,
            String status, Date dataInicio, Date dataFimPrevista) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.projetoId = projetoId;
        this.responsavelId = responsavelId;
        this.status = status;
        this.dataInicio = dataInicio;
        this.dataFimPrevista = dataFimPrevista;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getProjetoId() {
        return projetoId;
    }

    public void setProjetoId(int projetoId) {
        this.projetoId = projetoId;
    }

    public int getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(int responsavelId) {
        this.responsavelId = responsavelId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFimPrevista() {
        return dataFimPrevista;
    }

    public void setDataFimPrevista(Date dataFimPrevista) {
        this.dataFimPrevista = dataFimPrevista;
    }

    public Date getDataFimReal() {
        return dataFimReal;
    }

    public void setDataFimReal(Date dataFimReal) {
        this.dataFimReal = dataFimReal;
    }

    @Override
    public String toString() {
        return titulo + " - " + status;
    }
}