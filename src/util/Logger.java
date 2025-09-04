package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dao.ConexaoDAO;
import model.Usuario;

// Classe para registrar logs de ações do sistema
public class Logger {

    public static void logAcao(String acao, String tabela, int registroId) {
        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO logs_sistema (usuario_id, acao, tabela_afetada, registro_id) VALUES (?, ?, ?, ?)")) {

            Usuario usuario = Autenticacao.getUsuarioLogado();
            stmt.setInt(1, usuario != null ? usuario.getId() : 0);
            stmt.setString(2, acao);
            stmt.setString(3, tabela);
            stmt.setInt(4, registroId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao registrar log: " + e.getMessage());
        }
    }

    // Métodos específicos para diferentes tipos de ações
    public static void logCriacao(String tabela, int registroId) {
        logAcao("CRIAR", tabela, registroId);
    }

    public static void logEdicao(String tabela, int registroId) {
        logAcao("EDITAR", tabela, registroId);
    }

    public static void logExclusao(String tabela, int registroId) {
        logAcao("EXCLUIR", tabela, registroId);
    }

    public static void logLogin(int usuarioId) {
        logAcao("LOGIN", "usuarios", usuarioId);
    }
}
