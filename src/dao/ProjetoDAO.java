package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Projeto;

public class ProjetoDAO {

    // Insere um novo projeto no banco.
    public boolean inserirProjeto(Projeto projeto) {
        String sql = "INSERT INTO projetos (nome, descricao, data_inicio, data_termino_prevista, status, gerente_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setDate(3, new java.sql.Date(projeto.getDataInicio().getTime()));
            stmt.setDate(4, new java.sql.Date(projeto.getDataTerminoPrevista().getTime()));
            stmt.setString(5, projeto.getStatus());
            stmt.setInt(6, projeto.getGerenteId());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir projeto: " + e.getMessage());
            return false;
        }
    }

    // Retorna todos os projetos cadastrados.
    public List<Projeto> listarProjetos() {
        List<Projeto> projetos = new ArrayList<>();
        String sql = "SELECT * FROM projetos";

        try (Connection conn = ConexaoDAO.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Projeto projeto = new Projeto();
                projeto.setId(rs.getInt("id"));
                projeto.setNome(rs.getString("nome"));
                projeto.setDescricao(rs.getString("descricao"));
                projeto.setDataInicio(rs.getDate("data_inicio"));
                projeto.setDataTerminoPrevista(rs.getDate("data_termino_prevista"));
                projeto.setStatus(rs.getString("status"));
                projeto.setGerenteId(rs.getInt("gerente_id"));

                projetos.add(projeto);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar projetos: " + e.getMessage());
        }

        return projetos;
    }

    // Atualiza os dados de um projeto existente.
    public boolean atualizarProjeto(Projeto projeto) {
        String sql = "UPDATE projetos SET nome = ?, descricao = ?, data_inicio = ?, data_termino_prevista = ?, status = ?, gerente_id = ? WHERE id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setDate(3, new java.sql.Date(projeto.getDataInicio().getTime()));
            stmt.setDate(4, new java.sql.Date(projeto.getDataTerminoPrevista().getTime()));
            stmt.setString(5, projeto.getStatus());
            stmt.setInt(6, projeto.getGerenteId());
            stmt.setInt(7, projeto.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar projeto: " + e.getMessage());
            return false;
        }
    }

    // Remove um projeto pelo ID e todas as tarefas associadas.
    public boolean excluirProjeto(int id) {
        Connection conn = null;
        try {
            conn = ConexaoDAO.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // Primeiro exclui todas as tarefas do projeto
            String sqlTarefas = "DELETE FROM tarefas WHERE projeto_id = ?";
            try (PreparedStatement stmtTarefas = conn.prepareStatement(sqlTarefas)) {
                stmtTarefas.setInt(1, id);
                stmtTarefas.executeUpdate();
            }

            // Depois exclui o projeto
            String sqlProjeto = "DELETE FROM projetos WHERE id = ?";
            try (PreparedStatement stmtProjeto = conn.prepareStatement(sqlProjeto)) {
                stmtProjeto.setInt(1, id);
                int rowsAffected = stmtProjeto.executeUpdate();

                conn.commit();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Erro no rollback: " + rollbackEx.getMessage());
            }
            System.out.println("Erro ao excluir projeto: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    // Busca um projeto pelo ID.
    public Projeto buscarPorId(int id) {
        String sql = "SELECT * FROM projetos WHERE id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Projeto projeto = new Projeto();
                projeto.setId(rs.getInt("id"));
                projeto.setNome(rs.getString("nome"));
                projeto.setDescricao(rs.getString("descricao"));
                projeto.setDataInicio(rs.getDate("data_inicio"));
                projeto.setDataTerminoPrevista(rs.getDate("data_termino_prevista"));
                projeto.setStatus(rs.getString("status"));
                projeto.setGerenteId(rs.getInt("gerente_id"));
                return projeto;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar projeto: " + e.getMessage());
        }

        return null;
    }

    // Aloca uma equipe a um projeto.
    public boolean alocarEquipeProjeto(int projetoId, int equipeId) {
        String sql = "INSERT INTO projeto_equipes (projeto_id, equipe_id) VALUES (?, ?)";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projetoId);
            stmt.setInt(2, equipeId);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao alocar equipe: " + e.getMessage());
            return false;
        }
    }

    // Desaloca uma equipe de um projeto.
    public boolean desalocarEquipeProjeto(int projetoId, int equipeId) {
        String sql = "DELETE FROM projeto_equipes WHERE projeto_id = ? AND equipe_id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projetoId);
            stmt.setInt(2, equipeId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao desalocar equipe: " + e.getMessage());
            return false;
        }
    }

    // Lista todas as equipes alocadas a um projeto.
    public List<Integer> listarEquipesDoProjeto(int projetoId) {
        List<Integer> equipesIds = new ArrayList<>();
        String sql = "SELECT equipe_id FROM projeto_equipes WHERE projeto_id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projetoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                equipesIds.add(rs.getInt("equipe_id"));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar equipes do projeto: " + e.getMessage());
        }

        return equipesIds;
    }

    // Lista todos os projetos aos quais uma equipe está alocada.
    public List<Integer> listarProjetosDaEquipe(int equipeId) {
        List<Integer> projetosIds = new ArrayList<>();
        String sql = "SELECT projeto_id FROM projeto_equipes WHERE equipe_id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                projetosIds.add(rs.getInt("projeto_id"));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar projetos da equipe: " + e.getMessage());
        }

        return projetosIds;
    }
}