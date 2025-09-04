package model;

import java.util.Date;

public class HistoricoTarefa {
    private int id;
    private int tarefaId;
    private String statusAnterior;
    private String statusNovo;
    private int usuarioId;
    private Date dataAlteracao;
    private String observacao;

    public HistoricoTarefa() {
    }

    public HistoricoTarefa(int tarefaId, String statusAnterior, String statusNovo, int usuarioId, String observacao) {
        this.tarefaId = tarefaId;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.usuarioId = usuarioId;
        this.observacao = observacao;
        this.dataAlteracao = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTarefaId() {
        return tarefaId;
    }

    public void setTarefaId(int tarefaId) {
        this.tarefaId = tarefaId;
    }

    public String getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(String statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    public String getStatusNovo() {
        return statusNovo;
    }

    public void setStatusNovo(String statusNovo) {
        this.statusNovo = statusNovo;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
