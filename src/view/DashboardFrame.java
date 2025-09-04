package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import dao.ProjetoDAO;
import dao.TarefaDAO;
import dao.UsuarioDAO;
import model.Projeto;
import model.Tarefa;

// Frame para o dashboard do sistema
public class DashboardFrame extends JFrame {
    public DashboardFrame() {
        setTitle("Dashboard - Sistema de Gestão");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    // Inicializa os componentes da interface
    private void initComponents() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Dashboard de Gestão", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelMetricas = new JPanel(new GridLayout(2, 2, 10, 10));
        panelMetricas.add(criarCard("📊 Total de Projetos", getTotalProjetos(), Color.BLUE));
        panelMetricas.add(criarCard("✅ Tarefas Concluídas", getTarefasConcluidas(), Color.GREEN));
        panelMetricas.add(criarCard("👥 Total de Usuários", getTotalUsuarios(), Color.ORANGE));
        panelMetricas.add(criarCard("📋 Total de Tarefas", getTotalTarefas(), Color.RED));

        mainPanel.add(panelMetricas, BorderLayout.NORTH);

        mainPanel.add(criarPanelProjetosAndamento(), BorderLayout.CENTER);

        add(mainPanel);
    }

    // Cria um card para exibir uma métrica
    private JPanel criarCard(String titulo, String valor, Color cor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(cor, 2));
        card.setBackground(new Color(240, 240, 240));
        card.setPreferredSize(new Dimension(200, 100));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 24));
        lblValor.setForeground(cor);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    // Cria o painel que exibe os projetos em andamento
    private JPanel criarPanelProjetosAndamento() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Projetos em Andamento"));

        String[] colunas = { "ID", "Nome", "Status", "Término Previsto" };
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(model);

        ProjetoDAO projetoDAO = new ProjetoDAO();
        List<Projeto> projetos = projetoDAO.listarProjetos();

        // Adiciona apenas projetos com status "em_andamento"
        for (Projeto projeto : projetos) {
            if ("em_andamento".equals(projeto.getStatus())) {
                Object[] row = {
                        projeto.getId(),
                        projeto.getNome(),
                        projeto.getStatus(),
                        projeto.getDataTerminoPrevista()
                };
                model.addRow(row);
            }
        }

        panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return panel;
    }

    // Métodos para obter as métricas (simulados com dados estáticos ou consultas ao
    // banco)
    private String getTotalProjetos() {
        ProjetoDAO dao = new ProjetoDAO();
        return String.valueOf(dao.listarProjetos().size());
    }

    private String getTarefasConcluidas() {
        TarefaDAO dao = new TarefaDAO();
        List<Tarefa> tarefas = dao.listarTarefas();
        long concluidas = tarefas.stream().filter(t -> "concluida".equals(t.getStatus())).count();
        return concluidas + "/" + tarefas.size();
    }

    private String getTotalUsuarios() {
        UsuarioDAO dao = new UsuarioDAO();
        return String.valueOf(dao.listarUsuarios().size());
    }

    private String getTotalTarefas() {
        TarefaDAO dao = new TarefaDAO();
        return String.valueOf(dao.listarTarefas().size());
    }
}