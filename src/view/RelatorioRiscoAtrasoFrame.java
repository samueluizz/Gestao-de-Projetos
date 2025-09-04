package view;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import dao.ConexaoDAO;

// Frame para exibir o relatório de projetos com risco de atraso
public class RelatorioRiscoAtrasoFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    // Construtor
    public RelatorioRiscoAtrasoFrame() {
        setTitle("Projetos com Risco de Atraso");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columnNames = { "ID", "Nome do Projeto", "Data Término Prevista", "Dias de Atraso", "Gerente" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        carregarDados();
    }

    // Carrega os dados dos projetos com risco de atraso
    private void carregarDados() {
        try (Connection conn = ConexaoDAO.getConnection()) {
            String sql = "SELECT p.id, p.nome, p.data_termino_prevista, u.nome_completo as gerente " +
                    "FROM projetos p " +
                    "INNER JOIN usuarios u ON p.gerente_id = u.id " +
                    "WHERE p.status = 'em_andamento' " +
                    "AND p.data_termino_prevista IS NOT NULL " +
                    "AND p.data_termino_prevista < CURDATE() " +
                    "ORDER BY p.data_termino_prevista ASC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                Date dataTermino = rs.getDate("data_termino_prevista");
                String gerente = rs.getString("gerente");

                long diff = System.currentTimeMillis() - dataTermino.getTime();
                long diasAtraso = diff / (1000 * 60 * 60 * 24);

                Object[] row = { id, nome, dataTermino, diasAtraso, gerente };
                tableModel.addRow(row);
                count++;
            }

            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Nenhum projeto em risco de atraso encontrado.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar dados: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}