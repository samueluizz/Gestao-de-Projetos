package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Equipe;
import model.Usuario;

/**
 * Data Access Object (DAO) para operações CRUD de equipes no banco de dados.
 */

public class EquipeDAO {

    // Insere uma nova equipe e recupera o ID gerado
    public boolean inserirEquipe(Equipe equipe) {
        String sql = "INSERT INTO equipes (nome, descricao) VALUES (?, ?)";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    equipe.setId(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir equipe: " + e.getMessage());
        }

        return false;
    }

    // Retorna todas as equipes cadastradas, ordenadas por nome
    public List<Equipe> listarEquipes() {
        List<Equipe> equipes = new ArrayList<>();
        String sql = "SELECT * FROM equipes ORDER BY nome";

        try (Connection conn = ConexaoDAO.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Equipe equipe = new Equipe();
                equipe.setId(rs.getInt("id"));
                equipe.setNome(rs.getString("nome"));
                equipe.setDescricao(rs.getString("descricao"));

                equipes.add(equipe);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar equipes: " + e.getMessage());
        }

        return equipes;
    }

    // Atualiza dados de uma equipe existente
    public boolean atualizarEquipe(Equipe equipe) {
        String sql = "UPDATE equipes SET nome = ?, descricao = ? WHERE id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());
            stmt.setInt(3, equipe.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar equipe: " + e.getMessage());
            return false;
        }
    }

    // Remove equipe e seus membros vinculados
    public boolean excluirEquipe(int id) {
        excluirTodosMembros(id); // limpa membros antes de excluir a equipe

        String sql = "DELETE FROM equipes WHERE id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir equipe: " + e.getMessage());
            return false;
        }
    }

    // Busca equipe pelo ID
    public Equipe buscarPorId(int id) {
        String sql = "SELECT * FROM equipes WHERE id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Equipe equipe = new Equipe();
                equipe.setId(rs.getInt("id"));
                equipe.setNome(rs.getString("nome"));
                equipe.setDescricao(rs.getString("descricao"));
                return equipe;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar equipe: " + e.getMessage());
        }

        return null;
    }

    // Adiciona um usuário como membro de uma equipe
    public boolean adicionarMembro(int equipeId, int usuarioId) {
        String sql = "INSERT INTO equipe_membros (equipe_id, usuario_id) VALUES (?, ?)";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipeId);
            stmt.setInt(2, usuarioId);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao adicionar membro: " + e.getMessage());
            return false;
        }
    }

    // Remove um usuário de uma equipe
    public boolean removerMembro(int equipeId, int usuarioId) {
        String sql = "DELETE FROM equipe_membros WHERE equipe_id = ? AND usuario_id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipeId);
            stmt.setInt(2, usuarioId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao remover membro: " + e.getMessage());
            return false;
        }
    }

    // Lista todos os membros de uma equipe
    public List<Usuario> listarMembros(int equipeId) {
        List<Usuario> membros = new ArrayList<>();
        String sql = "SELECT u.* FROM usuarios u " +
                "INNER JOIN equipe_membros em ON u.id = em.usuario_id " +
                "WHERE em.equipe_id = ? ORDER BY u.nome_completo";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNomeCompleto(rs.getString("nome_completo"));
                usuario.setCpf(rs.getString("cpf"));
                usuario.setEmail(rs.getString("email"));
                usuario.setCargo(rs.getString("cargo"));
                usuario.setLogin(rs.getString("login"));
                usuario.setPerfil(rs.getString("perfil"));

                membros.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar membros: " + e.getMessage());
        }

        return membros;
    }

    // Remove todos os membros de uma equipe (usado antes da exclusão da equipe)
    private void excluirTodosMembros(int equipeId) {
        String sql = "DELETE FROM equipe_membros WHERE equipe_id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipeId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao excluir membros: " + e.getMessage());
        }
    }

    // Lista usuários que NÃO fazem parte de uma equipe
    public List<Usuario> listarNaoMembros(int equipeId) {
        List<Usuario> naoMembros = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE id NOT IN " +
                "(SELECT usuario_id FROM equipe_membros WHERE equipe_id = ?) " +
                "ORDER BY nome_completo";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNomeCompleto(rs.getString("nome_completo"));
                usuario.setCpf(rs.getString("cpf"));
                usuario.setEmail(rs.getString("email"));
                usuario.setCargo(rs.getString("cargo"));
                usuario.setLogin(rs.getString("login"));
                usuario.setPerfil(rs.getString("perfil"));

                naoMembros.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar não membros: " + e.getMessage());
        }

        return naoMembros;
    }
}