package model;

import java.util.List;

public class Equipe {
    private int id;
    private String nome;
    private String descricao;
    private List<Usuario> membros;
    private List<Projeto> projetos;

    public Equipe() {
    }

    public Equipe(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
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

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }

    public List<Projeto> getProjetos() {
        return projetos;
    }

    public void setProjetos(List<Projeto> projetos) {
        this.projetos = projetos;
    }

    public void adicionarMembro(Usuario usuario) {
        membros.add(usuario);
    }

    public void removerMembro(Usuario usuario) {
        membros.remove(usuario);
    }

    @Override
    public String toString() {
        return nome + " (" + membros.size() + " membros)";
    }
}