package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import dao.EquipeDAO;
import dao.ProjetoDAO;
import model.Equipe;

// Frame para alocar equipes a um projeto
public class AlocarEquipesFrame extends JFrame {

    // Atributos
    private int projetoId;
    private String projetoNome;

    // Componentes Swing
    private JTable tabelaEquipesDisponiveis, tabelaEquipesAlocadas;
    private DefaultTableModel modelDisponiveis, modelAlocadas;
    private JButton btnAlocar, btnDesalocar, btnFechar;

    // Construtor
    public AlocarEquipesFrame(int projetoId, String projetoNome) {
        this.projetoId = projetoId;
        this.projetoNome = projetoNome;

        setTitle("Alocar Equipes - " + projetoNome);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        carregarDados();
    }

    // Inicializa os componentes da interface
    private void initComponents() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Alocar Equipes ao Projeto: " + projetoNome, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelTabelas = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel panelDisponiveis = new JPanel(new BorderLayout());
        panelDisponiveis.setBorder(BorderFactory.createTitledBorder("Equipes Disponíveis"));
        String[] colunas = { "ID", "Nome", "Descrição" };
        modelDisponiveis = new DefaultTableModel(colunas, 0);
        tabelaEquipesDisponiveis = new JTable(modelDisponiveis);
        panelDisponiveis.add(new JScrollPane(tabelaEquipesDisponiveis), BorderLayout.CENTER);

        JPanel panelAlocadas = new JPanel(new BorderLayout());
        panelAlocadas.setBorder(BorderFactory.createTitledBorder("Equipes Alocadas"));
        modelAlocadas = new DefaultTableModel(colunas, 0);
        tabelaEquipesAlocadas = new JTable(modelAlocadas);
        panelAlocadas.add(new JScrollPane(tabelaEquipesAlocadas), BorderLayout.CENTER);

        panelTabelas.add(panelDisponiveis);
        panelTabelas.add(panelAlocadas);

        JPanel panelBotoes = new JPanel(new FlowLayout());
        btnAlocar = new JButton("Alocar →");
        btnDesalocar = new JButton("← Desalocar");
        btnFechar = new JButton("Fechar");

        panelBotoes.add(btnAlocar);
        panelBotoes.add(btnDesalocar);
        panelBotoes.add(btnFechar);

        mainPanel.add(panelTabelas, BorderLayout.CENTER);
        mainPanel.add(panelBotoes, BorderLayout.SOUTH);

        add(mainPanel);

        btnAlocar.addActionListener(e -> alocarEquipe());
        btnDesalocar.addActionListener(e -> desalocarEquipe());
        btnFechar.addActionListener(e -> dispose());
    }

    // Carrega os dados iniciais
    private void carregarDados() {
        carregarEquipesDisponiveis();
        carregarEquipesAlocadas();
    }

    // Carrega as equipes disponíveis para alocação
    private void carregarEquipesDisponiveis() {
        modelDisponiveis.setRowCount(0);
        EquipeDAO equipeDAO = new EquipeDAO();
        ProjetoDAO projetoDAO = new ProjetoDAO();
        List<Equipe> todasEquipes = equipeDAO.listarEquipes();
        List<Integer> equipesAlocadas = projetoDAO.listarEquipesDoProjeto(projetoId);

        // Filtra as equipes que já estão alocadas
        for (Equipe equipe : todasEquipes) {
            if (!equipesAlocadas.contains(equipe.getId())) {
                Object[] row = { equipe.getId(), equipe.getNome(), equipe.getDescricao() };
                modelDisponiveis.addRow(row);
            }
        }
    }

    // Carrega as equipes já alocadas ao projeto
    private void carregarEquipesAlocadas() {
        modelAlocadas.setRowCount(0);
        ProjetoDAO projetoDAO = new ProjetoDAO();
        EquipeDAO equipeDAO = new EquipeDAO();
        List<Integer> equipesIds = projetoDAO.listarEquipesDoProjeto(projetoId);

        // Adiciona as equipes alocadas na tabela
        for (Integer equipeId : equipesIds) {
            Equipe equipe = equipeDAO.buscarPorId(equipeId);
            if (equipe != null) {
                Object[] row = { equipe.getId(), equipe.getNome(), equipe.getDescricao() };
                modelAlocadas.addRow(row);
            }
        }
    }

    // Aloca a equipe selecionada ao projeto
    private void alocarEquipe() {
        int selectedRow = tabelaEquipesDisponiveis.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma equipe para alocar!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int equipeId = (Integer) modelDisponiveis.getValueAt(selectedRow, 0);
        String equipeNome = (String) modelDisponiveis.getValueAt(selectedRow, 1);

        ProjetoDAO projetoDAO = new ProjetoDAO();
        if (projetoDAO.alocarEquipeProjeto(projetoId, equipeId)) {
            JOptionPane.showMessageDialog(this, "Equipe " + equipeNome + " alocada com sucesso!");
            carregarDados();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao alocar equipe!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Desaloca a equipe selecionada do projeto
    private void desalocarEquipe() {
        int selectedRow = tabelaEquipesAlocadas.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma equipe para desalocar!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int equipeId = (Integer) modelAlocadas.getValueAt(selectedRow, 0);
        String equipeNome = (String) modelAlocadas.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Desalocar equipe " + equipeNome + " do projeto?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ProjetoDAO projetoDAO = new ProjetoDAO();
            if (projetoDAO.desalocarEquipeProjeto(projetoId, equipeId)) {
                JOptionPane.showMessageDialog(this, "Equipe desalocada com sucesso!");
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao desalocar equipe!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}