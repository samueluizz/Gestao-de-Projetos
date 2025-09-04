package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.HistoricoTarefa;
import model.Tarefa;
import util.Autenticacao;

public class TarefaDAO {

    /**
     * Insere uma nova tarefa no banco.
     */
    public boolean inserirTarefa(Tarefa tarefa) {
        String sql = "INSERT INTO tarefas (titulo, descricao, projeto_id, responsavel_id, status, data_inicio, data_fim_prevista, data_fim_real) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, tarefa.getProjetoId());
            stmt.setInt(4, tarefa.getResponsavelId());
            stmt.setString(5, tarefa.getStatus());

            // Verifica se data_inicio e data_fim_prevista são nulas antes de definir
            if (tarefa.getDataInicio() != null) {
                stmt.setDate(6, new java.sql.Date(tarefa.getDataInicio().getTime()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            // Verifica se data_fim_prevista é nula antes de definir
            if (tarefa.getDataFimPrevista() != null) {
                stmt.setDate(7, new java.sql.Date(tarefa.getDataFimPrevista().getTime()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            // Preenche data_fim_real se status for "concluida"
            if ("concluida".equals(tarefa.getStatus())) {
                stmt.setDate(8, new java.sql.Date(new Date().getTime()));
            } else {
                stmt.setNull(8, Types.DATE);
            }

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Obter o ID gerado
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    tarefa.setId(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir tarefa: " + e.getMessage());
        }

        return false;
    }

    // Retorna todas as tarefas cadastradas.
    public List<Tarefa> listarTarefas() {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas ORDER BY status, data_fim_prevista";

        try (Connection conn = ConexaoDAO.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId(rs.getInt("id"));
                tarefa.setTitulo(rs.getString("titulo"));
                tarefa.setDescricao(rs.getString("descricao"));
                tarefa.setProjetoId(rs.getInt("projeto_id"));
                tarefa.setResponsavelId(rs.getInt("responsavel_id"));
                tarefa.setStatus(rs.getString("status"));
                tarefa.setDataInicio(rs.getDate("data_inicio"));
                tarefa.setDataFimPrevista(rs.getDate("data_fim_prevista"));
                tarefa.setDataFimReal(rs.getDate("data_fim_real"));

                tarefas.add(tarefa);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar tarefas: " + e.getMessage());
        }

        return tarefas;
    }

    // Atualiza os dados de uma tarefa existente.
    public boolean atualizarTarefa(Tarefa tarefa) {
        String sql = "UPDATE tarefas SET titulo = ?, descricao = ?, projeto_id = ?, responsavel_id = ?, " +
                "status = ?, data_inicio = ?, data_fim_prevista = ?, data_fim_real = ? WHERE id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, tarefa.getProjetoId());
            stmt.setInt(4, tarefa.getResponsavelId());
            stmt.setString(5, tarefa.getStatus());

            if (tarefa.getDataInicio() != null) {
                stmt.setDate(6, new java.sql.Date(tarefa.getDataInicio().getTime()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            if (tarefa.getDataFimPrevista() != null) {
                stmt.setDate(7, new java.sql.Date(tarefa.getDataFimPrevista().getTime()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            // Preencher data_fim_real se status for "concluida"
            if ("concluida".equals(tarefa.getStatus())) {
                // Se já tinha data de conclusão, mantém. Se não, preenche com data atual.
                if (tarefa.getDataFimReal() != null) {
                    stmt.setDate(8, new java.sql.Date(tarefa.getDataFimReal().getTime()));
                } else {
                    stmt.setDate(8, new java.sql.Date(System.currentTimeMillis()));
                }
            } else {
                // Se mudou de "concluida" para outro status, limpa a data de conclusão
                stmt.setNull(8, Types.DATE);
            }

            stmt.setInt(9, tarefa.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar tarefa: " + e.getMessage());
            return false;
        }
    }

    // Remove uma tarefa pelo ID.
    public boolean excluirTarefa(int id) {
        String sql = "DELETE FROM tarefas WHERE id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir tarefa: " + e.getMessage());
            return false;
        }
    }

    // Busca uma tarefa pelo ID.
    public Tarefa buscarPorId(int id) {
        String sql = "SELECT * FROM tarefas WHERE id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId(rs.getInt("id"));
                tarefa.setTitulo(rs.getString("titulo"));
                tarefa.setDescricao(rs.getString("descricao"));
                tarefa.setProjetoId(rs.getInt("projeto_id"));
                tarefa.setResponsavelId(rs.getInt("responsavel_id"));
                tarefa.setStatus(rs.getString("status"));
                tarefa.setDataInicio(rs.getDate("data_inicio"));
                tarefa.setDataFimPrevista(rs.getDate("data_fim_prevista"));
                tarefa.setDataFimReal(rs.getDate("data_fim_real"));
                return tarefa;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar tarefa: " + e.getMessage());
        }

        return null;
    }

    // Marca uma tarefa como concluída.
    public boolean concluirTarefa(int id) {
        String sql = "UPDATE tarefas SET status = 'concluida', data_fim_real = CURRENT_DATE WHERE id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao concluir tarefa: " + e.getMessage());
            return false;
        }
    }

    // Lista todas as tarefas atribuídas a um usuário específico.
    public List<Tarefa> listarTarefasPorUsuario(int usuarioId) {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas WHERE responsavel_id = ?";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId(rs.getInt("id"));
                tarefa.setTitulo(rs.getString("titulo"));
                tarefa.setDescricao(rs.getString("descricao"));
                tarefa.setProjetoId(rs.getInt("projeto_id"));
                tarefa.setResponsavelId(rs.getInt("responsavel_id"));
                tarefa.setStatus(rs.getString("status"));
                tarefa.setDataInicio(rs.getDate("data_inicio"));
                tarefa.setDataFimPrevista(rs.getDate("data_fim_prevista"));
                tarefa.setDataFimReal(rs.getDate("data_fim_real"));

                tarefas.add(tarefa);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar tarefas por usuário: " + e.getMessage());
        }

        return tarefas;
    }

    // Atualiza o status de uma tarefa e registra o histórico da alteração.
    public boolean atualizarStatusTarefa(int tarefaId, String novoStatus, String observacao) {
        try {
            // Buscar tarefa atual
            Tarefa tarefaAtual = buscarPorId(tarefaId);
            if (tarefaAtual == null) {
                return false;
            }

            String statusAnterior = tarefaAtual.getStatus();

            // Atualizar status da tarefa
            tarefaAtual.setStatus(novoStatus);
            boolean sucesso = atualizarTarefa(tarefaAtual);

            if (sucesso) {
                HistoricoTarefa historico = new HistoricoTarefa(
                        tarefaId,
                        statusAnterior,
                        novoStatus,
                        Autenticacao.getUsuarioLogado().getId(),
                        observacao);

                HistoricoTarefaDAO historicoDAO = new HistoricoTarefaDAO();
                historicoDAO.registrarAlteracao(historico);
            }

            return sucesso;

        } catch (Exception e) {
            System.out.println("Erro ao atualizar status da tarefa: " + e.getMessage());
            return false;
        }
    }

    // Lista todas as tarefas de um projeto específico.
    public List<Tarefa> listarTarefasPorProjeto(int projetoId) {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas WHERE projeto_id = ? ORDER BY status, data_fim_prevista";

        try (Connection conn = ConexaoDAO.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projetoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId(rs.getInt("id"));
                tarefa.setTitulo(rs.getString("titulo"));
                tarefa.setDescricao(rs.getString("descricao"));
                tarefa.setProjetoId(rs.getInt("projeto_id"));
                tarefa.setResponsavelId(rs.getInt("responsavel_id"));
                tarefa.setStatus(rs.getString("status"));
                tarefa.setDataInicio(rs.getDate("data_inicio"));
                tarefa.setDataFimPrevista(rs.getDate("data_fim_prevista"));
                tarefa.setDataFimReal(rs.getDate("data_fim_real"));

                tarefas.add(tarefa);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar tarefas por projeto: " + e.getMessage());
        }

        return tarefas;
    }
}