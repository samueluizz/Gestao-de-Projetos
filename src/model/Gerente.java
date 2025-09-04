package model;

public class Gerente extends Usuario {
    public Gerente() {
        super();
        setPerfil("gerente");
    }

    public Gerente(String nomeCompleto, String cpf, String email,
            String cargo, String login, String senha) {
        super(nomeCompleto, cpf, email, cargo, login, senha, "gerente");
    }

    public void aprovarTarefa(Tarefa tarefa) {
        System.out.println("Tarefa " + tarefa.getTitulo() + " aprovada pelo gerente " + getNomeCompleto());
    }
}