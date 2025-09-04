package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.HistoricoTarefa;

public class HistoricoTarefaDAO {

    // Insere um novo registro de alteração de tarefa no banco
    public boolean registrarAlteracao(HistoricoTarefa historico) {
        String sql = "INSERT INTO historico_tarefas (tarefa_id, status_anterior, status_novo, usuario_id, observacao) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, historico.getTarefaId());
            stmt.setString(2, historico.getStatusAnterior());
            stmt.setString(3, historico.getStatusNovo());
            stmt.setInt(4, historico.getUsuarioId());
            stmt.setString(5, historico.getObservacao());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao registrar histórico: " + e.getMessage());
            return false;
        }
    }

    // Retorna o histórico de alterações de uma tarefa específica
    public List<HistoricoTarefa> listarHistoricoTarefa(int tarefaId) {
        List<HistoricoTarefa> historicos = new ArrayList<>();
        String sql = "SELECT h.*, u.nome_completo FROM historico_tarefas h " +
                "INNER JOIN usuarios u ON h.usuario_id = u.id " +
                "WHERE h.tarefa_id = ? ORDER BY h.data_alteracao DESC";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tarefaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HistoricoTarefa historico = new HistoricoTarefa();
                historico.setId(rs.getInt("id"));
                historico.setTarefaId(rs.getInt("tarefa_id"));
                historico.setStatusAnterior(rs.getString("status_anterior"));
                historico.setStatusNovo(rs.getString("status_novo"));
                historico.setUsuarioId(rs.getInt("usuario_id"));
                historico.setDataAlteracao(rs.getTimestamp("data_alteracao"));
                historico.setObservacao(rs.getString("observacao"));

                historicos.add(historico);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar histórico: " + e.getMessage());
        }

        return historicos;
    }
}
