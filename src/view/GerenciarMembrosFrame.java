package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import model.Usuario;

// Frame para gerenciar membros de uma equipe
public class GerenciarMembrosFrame extends JFrame {
    private int equipeId;
    private String equipeNome;

    private JTable tabelaMembros, tabelaNaoMembros;
    private DefaultTableModel modelMembros, modelNaoMembros;
    private JButton btnAdicionar, btnRemover, btnFechar;

    // Construtor
    public GerenciarMembrosFrame(int equipeId, String equipeNome) {
        this.equipeId = equipeId;
        this.equipeNome = equipeNome;

        setTitle("Gerenciar Membros - " + equipeNome);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        carregarDados();
    }

    // Inicializa os componentes da interface
    private void initComponents() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Gerenciar Membros da Equipe: " + equipeNome, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelTabelas = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel panelNaoMembros = new JPanel(new BorderLayout());
        panelNaoMembros.setBorder(BorderFactory.createTitledBorder("Usuários Disponíveis"));

        String[] colunas = { "ID", "Nome", "Cargo", "Perfil" };
        modelNaoMembros = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaNaoMembros = new JTable(modelNaoMembros);
        panelNaoMembros.add(new JScrollPane(tabelaNaoMembros), BorderLayout.CENTER);

        JPanel panelMembros = new JPanel(new BorderLayout());
        panelMembros.setBorder(BorderFactory.createTitledBorder("Membros da Equipe"));

        modelMembros = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaMembros = new JTable(modelMembros);
        panelMembros.add(new JScrollPane(tabelaMembros), BorderLayout.CENTER);

        panelTabelas.add(panelNaoMembros);
        panelTabelas.add(panelMembros);

        JPanel panelBotoes = new JPanel(new FlowLayout());
        btnAdicionar = new JButton("Adicionar →");
        btnRemover = new JButton("← Remover");
        btnFechar = new JButton("Fechar");

        panelBotoes.add(btnAdicionar);
        panelBotoes.add(btnRemover);
        panelBotoes.add(btnFechar);

        mainPanel.add(panelTabelas, BorderLayout.CENTER);
        mainPanel.add(panelBotoes, BorderLayout.SOUTH);

        add(mainPanel);

        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarMembro();
            }
        });

        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removerMembro();
            }
        });

        btnFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void carregarDados() {
        carregarNaoMembros();
        carregarMembros();
    }

    private void carregarNaoMembros() {
        modelNaoMembros.setRowCount(0);
        EquipeDAO equipeDAO = new EquipeDAO();
        List<Usuario> naoMembros = equipeDAO.listarNaoMembros(equipeId);

        for (Usuario usuario : naoMembros) {
            Object[] row = {
                    usuario.getId(),
                    usuario.getNomeCompleto(),
                    usuario.getCargo(),
                    usuario.getPerfil()
            };
            modelNaoMembros.addRow(row);
        }
    }

    private void carregarMembros() {
        modelMembros.setRowCount(0);
        EquipeDAO equipeDAO = new EquipeDAO();
        List<Usuario> membros = equipeDAO.listarMembros(equipeId);

        for (Usuario usuario : membros) {
            Object[] row = {
                    usuario.getId(),
                    usuario.getNomeCompleto(),
                    usuario.getCargo(),
                    usuario.getPerfil()
            };
            modelMembros.addRow(row);
        }
    }

    // Adiciona um membro à equipe
    private void adicionarMembro() {
        int selectedRow = tabelaNaoMembros.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para adicionar!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int usuarioId = (Integer) modelNaoMembros.getValueAt(selectedRow, 0);
        String usuarioNome = (String) modelNaoMembros.getValueAt(selectedRow, 1);

        EquipeDAO equipeDAO = new EquipeDAO();
        if (equipeDAO.adicionarMembro(equipeId, usuarioId)) {
            JOptionPane.showMessageDialog(this, "Usuário " + usuarioNome + " adicionado à equipe!");
            carregarDados();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Remove um membro da equipe
    private void removerMembro() {
        int selectedRow = tabelaMembros.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um membro para remover!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int usuarioId = (Integer) modelMembros.getValueAt(selectedRow, 0);
        String usuarioNome = (String) modelMembros.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover " + usuarioNome + " da equipe?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            EquipeDAO equipeDAO = new EquipeDAO();
            if (equipeDAO.removerMembro(equipeId, usuarioId)) {
                JOptionPane.showMessageDialog(this, "Usuário " + usuarioNome + " removido da equipe!");
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao remover usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}