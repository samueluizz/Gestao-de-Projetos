package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import dao.TarefaDAO;
import dao.UsuarioDAO;
import model.Usuario;

// Frame para exibir o relatório de desempenho dos colaboradores
public class RelatorioDesempenhoFrame extends JFrame {
    public RelatorioDesempenhoFrame() {
        setTitle("Relatório de Desempenho");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    // Inicializa os componentes da interface
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Desempenho por Colaborador", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        String[] colunas = { "Usuário", "Perfil", "Tarefas Totais", "Tarefas Concluídas", "Taxa de Conclusão" };
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(model);

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        TarefaDAO tarefaDAO = new TarefaDAO();

        List<Usuario> usuarios = usuarioDAO.listarUsuarios();

        // Popula a tabela com os dados de desempenho
        for (Usuario usuario : usuarios) {
            int totalTarefas = tarefaDAO.listarTarefasPorUsuario(usuario.getId()).size();
            int tarefasConcluidas = (int) tarefaDAO.listarTarefasPorUsuario(usuario.getId()).stream()
                    .filter(t -> "concluida".equals(t.getStatus())).count();

            double taxaConclusao = totalTarefas > 0 ? (tarefasConcluidas * 100.0 / totalTarefas) : 0;

            Object[] row = {
                    usuario.getNomeCompleto(),
                    usuario.getPerfil(),
                    totalTarefas,
                    tarefasConcluidas,
                    String.format("%.1f%%", taxaConclusao)
            };
            model.addRow(row);
        }

        mainPanel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(mainPanel);
    }
}