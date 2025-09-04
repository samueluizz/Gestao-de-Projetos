package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.EquipeDAO;
import model.Equipe;

// Frame para gerenciar equipes
public class EquipeFrame extends JFrame {
    private JTextField txtNome, txtDescricao;
    private JButton btnSalvar, btnEditar, btnExcluir, btnLimpar, btnGerenciarMembros;
    private JTable tabelaEquipes;
    private DefaultTableModel tableModel;
    private int equipeEditandoId = -1;

    // Construtor
    public EquipeFrame() {
        setTitle("Cadastro de Equipes");
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel panelForm = new JPanel(new GridLayout(3, 2, 5, 5));

        panelForm.add(new JLabel("Nome da Equipe:*"));
        txtNome = new JTextField();
        panelForm.add(txtNome);

        panelForm.add(new JLabel("Descrição:"));
        txtDescricao = new JTextField();
        panelForm.add(txtDescricao);

        JPanel panelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");
        btnGerenciarMembros = new JButton("Gerenciar Membros");

        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnEditar);
        panelBotoes.add(btnExcluir);
        panelBotoes.add(btnLimpar);
        panelBotoes.add(btnGerenciarMembros);

        String[] colunas = { "ID", "Nome", "Descrição", "Quantidade de Membros" };
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEquipes = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelaEquipes);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(panelForm, BorderLayout.NORTH);
        topPanel.add(panelBotoes, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        carregarEquipes();

        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (equipeEditandoId == -1) {
                    salvarEquipe();
                } else {
                    editarEquipe();
                }
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prepararEdicao();
            }
        });

        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirEquipe();
            }
        });

        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparFormulario();
            }
        });

        btnGerenciarMembros.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("DEBUG: Botão Gerenciar Membros clicado!");
                gerenciarMembros();
            }
        });

        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnGerenciarMembros.setEnabled(false);

        tabelaEquipes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = tabelaEquipes.getSelectedRow() >= 0;
                btnEditar.setEnabled(hasSelection);
                btnExcluir.setEnabled(hasSelection);
                btnGerenciarMembros.setEnabled(hasSelection);
            }
        });
    }

    // Carrega as equipes do banco e exibe na tabela
    private void carregarEquipes() {
        tableModel.setRowCount(0);
        EquipeDAO equipeDAO = new EquipeDAO();
        List<Equipe> equipes = equipeDAO.listarEquipes();

        for (Equipe equipe : equipes) {
            List<model.Usuario> membros = equipeDAO.listarMembros(equipe.getId());

            Object[] row = {
                    equipe.getId(),
                    equipe.getNome(),
                    equipe.getDescricao(),
                    membros.size() + " membro(s)"
            };
            tableModel.addRow(row);
        }
    }

    // Salva uma nova equipe
    private void salvarEquipe() {
        String nome = txtNome.getText().trim();
        String descricao = txtDescricao.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome da equipe é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Equipe equipe = new Equipe(nome, descricao);
        EquipeDAO equipeDAO = new EquipeDAO();

        if (equipeDAO.inserirEquipe(equipe)) {
            JOptionPane.showMessageDialog(this, "Equipe salva com sucesso!");
            limparFormulario();
            carregarEquipes();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar equipe!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Prepara o formulário para edição da equipe selecionada
    private void prepararEdicao() {
        int selectedRow = tabelaEquipes.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma equipe para editar!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int equipeId = (Integer) tableModel.getValueAt(selectedRow, 0);
            EquipeDAO equipeDAO = new EquipeDAO();
            Equipe equipe = equipeDAO.buscarPorId(equipeId);

            if (equipe != null) {
                equipeEditandoId = equipeId;
                txtNome.setText(equipe.getNome());
                txtDescricao.setText(equipe.getDescricao());

                btnSalvar.setText("Atualizar");
                btnEditar.setEnabled(false);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar equipe: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Edita a equipe atualmente em edição
    private void editarEquipe() {
        String nome = txtNome.getText().trim();
        String descricao = txtDescricao.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome da equipe é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Equipe equipe = new Equipe(nome, descricao);
        equipe.setId(equipeEditandoId);

        EquipeDAO equipeDAO = new EquipeDAO();
        if (equipeDAO.atualizarEquipe(equipe)) {
            JOptionPane.showMessageDialog(this, "Equipe atualizada com sucesso!");
            limparFormulario();
            carregarEquipes();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar equipe!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Exclui a equipe selecionada
    private void excluirEquipe() {
        int selectedRow = tabelaEquipes.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma equipe para excluir!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int equipeId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String nomeEquipe = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a equipe:\n" + nomeEquipe
                        + "?\n\nTodos os membros serão removidos da equipe.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            EquipeDAO equipeDAO = new EquipeDAO();
            if (equipeDAO.excluirEquipe(equipeId)) {
                JOptionPane.showMessageDialog(this, "Equipe excluída com sucesso!");
                limparFormulario();
                carregarEquipes();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir equipe!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Abre o frame para gerenciar membros da equipe selecionada
    private void gerenciarMembros() {
        System.out.println("DEBUG: Método gerenciarMembros() iniciado");

        int selectedRow = tabelaEquipes.getSelectedRow();
        if (selectedRow < 0) {
            System.out.println("DEBUG: Nenhuma equipe selecionada");
            JOptionPane.showMessageDialog(this, "Selecione uma equipe para gerenciar membros!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("DEBUG: Equipe selecionada na linha " + selectedRow);

        int equipeId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String nomeEquipe = (String) tableModel.getValueAt(selectedRow, 1);

        System.out.println("DEBUG: Abrindo GerenciarMembrosFrame para equipe ID: " + equipeId);

        GerenciarMembrosFrame membrosFrame = new GerenciarMembrosFrame(equipeId, nomeEquipe);
        membrosFrame.setVisible(true);

        membrosFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                System.out.println("DEBUG: GerenciarMembrosFrame fechado, recarregando equipes");
                carregarEquipes();
            }
        });

        System.out.println("DEBUG: GerenciarMembrosFrame aberto com sucesso");
    }

    // Limpa o formulário e reseta o estado
    private void limparFormulario() {
        txtNome.setText("");
        txtDescricao.setText("");
        equipeEditandoId = -1;
        btnSalvar.setText("Salvar");
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnGerenciarMembros.setEnabled(false);
        tabelaEquipes.clearSelection();
    }
}