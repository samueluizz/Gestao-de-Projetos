package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.ProjetoDAO;
import dao.TarefaDAO;
import dao.UsuarioDAO;
import model.Projeto;
import model.Tarefa;
import model.Usuario;

// Frame para o cadastro e gerenciamento de projetos
public class ProjetoFrame extends JFrame {
    private JTextField txtNome, txtDescricao;
    private JComboBox<String> cbStatus, cbGerente;
    private JFormattedTextField txtDataInicio, txtDataTermino;
    private JButton btnSalvar, btnEditar, btnExcluir, btnLimpar;
    private JTable tabelaProjetos;
    private DefaultTableModel tableModel;
    private Map<String, Integer> gerentesMap = new HashMap<>();
    private int projetoEditandoId = -1;

    // Construtor
    public ProjetoFrame() {
        setTitle("Cadastro de Projetos");
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JPanel panelForm = new JPanel(new GridLayout(6, 2, 5, 5));

        panelForm.add(new JLabel("Nome do Projeto:*"));
        txtNome = new JTextField();
        panelForm.add(txtNome);

        panelForm.add(new JLabel("Descrição:"));
        txtDescricao = new JTextField();
        panelForm.add(txtDescricao);

        panelForm.add(new JLabel("Data Início:*"));
        txtDataInicio = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        txtDataInicio.setValue(new Date());
        panelForm.add(txtDataInicio);

        panelForm.add(new JLabel("Data Término Previsto:*"));
        txtDataTermino = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        panelForm.add(txtDataTermino);

        panelForm.add(new JLabel("Status:*"));
        cbStatus = new JComboBox<>(new String[] { "planejado", "em_andamento", "concluido", "cancelado" });
        panelForm.add(cbStatus);

        panelForm.add(new JLabel("Gerente Responsável:*"));
        cbGerente = new JComboBox<>();
        carregarGerentes();
        panelForm.add(cbGerente);

        JPanel panelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");

        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnEditar);
        panelBotoes.add(btnExcluir);
        panelBotoes.add(btnLimpar);

        JButton btnGerenciarEquipes = new JButton("Gerenciar Equipes");
        JButton btnCancelarProjeto = new JButton("Cancelar Projeto");

        panelBotoes.add(btnGerenciarEquipes);
        panelBotoes.add(btnCancelarProjeto);

        String[] colunas = { "ID", "Nome", "Descrição", "Início", "Término", "Status", "Gerente" };
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProjetos = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelaProjetos);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(panelForm, BorderLayout.NORTH);
        topPanel.add(panelBotoes, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        carregarProjetos();

        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (projetoEditandoId == -1) {
                    salvarProjeto();
                } else {
                    editarProjeto();
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
                excluirProjeto();
            }
        });

        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparFormulario();
            }
        });

        btnGerenciarEquipes.addActionListener(e -> gerenciarEquipesProjeto());
        btnCancelarProjeto.addActionListener(e -> cancelarProjeto());

        tabelaProjetos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnEditar.setEnabled(tabelaProjetos.getSelectedRow() >= 0);
                btnExcluir.setEnabled(tabelaProjetos.getSelectedRow() >= 0);
            }
        });
    }

    // Carrega os gerentes no combo box
    private void carregarGerentes() {
        cbGerente.removeAllItems();
        gerentesMap.clear();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.listarUsuarios();

        for (Usuario usuario : usuarios) {
            String display = usuario.getNomeCompleto() + " (" + usuario.getPerfil() + ")";
            cbGerente.addItem(display);
            gerentesMap.put(display, usuario.getId());
        }
    }

    // Carrega os projetos na tabela
    private void carregarProjetos() {
        tableModel.setRowCount(0);
        ProjetoDAO projetoDAO = new ProjetoDAO();
        List<Projeto> projetos = projetoDAO.listarProjetos();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        for (Projeto projeto : projetos) {
            Usuario gerente = usuarioDAO.buscarPorId(projeto.getGerenteId());
            String nomeGerente = gerente != null ? gerente.getNomeCompleto() : "N/A";

            Object[] row = {
                    projeto.getId(),
                    projeto.getNome(),
                    projeto.getDescricao(),
                    sdf.format(projeto.getDataInicio()),
                    sdf.format(projeto.getDataTerminoPrevista()),
                    projeto.getStatus(),
                    nomeGerente
            };
            tableModel.addRow(row);
        }
    }

    // Salva um novo projeto no banco de dados
    private void salvarProjeto() {
        try {
            String nome = txtNome.getText().trim();
            String descricao = txtDescricao.getText().trim();
            Date dataInicio = (Date) txtDataInicio.getValue();
            Date dataTermino = (Date) txtDataTermino.getValue();
            String status = (String) cbStatus.getSelectedItem();
            int gerenteId = gerentesMap.get((String) cbGerente.getSelectedItem());

            // Validações básicas
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome do projeto é obrigatório!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dataInicio == null || dataTermino == null) {
                JOptionPane.showMessageDialog(this, "Datas são obrigatórias!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dataTermino.before(dataInicio)) {
                JOptionPane.showMessageDialog(this, "Data de término deve ser após a data de início!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Projeto projeto = new Projeto(nome, descricao, dataInicio, dataTermino, status, gerenteId);
            ProjetoDAO projetoDAO = new ProjetoDAO();

            if (projetoDAO.inserirProjeto(projeto)) {
                JOptionPane.showMessageDialog(this, "Projeto salvo com sucesso!");
                limparFormulario();
                carregarProjetos();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar projeto!", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Prepara o formulário para edição do projeto selecionado
    private void prepararEdicao() {
        int selectedRow = tabelaProjetos.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto para editar!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int projetoId = (Integer) tableModel.getValueAt(selectedRow, 0);
            ProjetoDAO projetoDAO = new ProjetoDAO();
            Projeto projeto = projetoDAO.buscarPorId(projetoId);

            if (projeto != null) {
                projetoEditandoId = projetoId;
                txtNome.setText(projeto.getNome());
                txtDescricao.setText(projeto.getDescricao());
                txtDataInicio.setValue(projeto.getDataInicio());
                txtDataTermino.setValue(projeto.getDataTerminoPrevista());
                cbStatus.setSelectedItem(projeto.getStatus());

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                Usuario gerente = usuarioDAO.buscarPorId(projeto.getGerenteId());
                if (gerente != null) {
                    String gerenteDisplay = gerente.getNomeCompleto() + " (" + gerente.getPerfil() + ")";
                    cbGerente.setSelectedItem(gerenteDisplay);
                }

                btnSalvar.setText("Atualizar");
                btnEditar.setEnabled(false);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar projeto: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Edita o projeto no banco de dados
    private void editarProjeto() {
        try {
            String nome = txtNome.getText().trim();
            String descricao = txtDescricao.getText().trim();
            Date dataInicio = (Date) txtDataInicio.getValue();
            Date dataTermino = (Date) txtDataTermino.getValue();
            String status = (String) cbStatus.getSelectedItem();
            int gerenteId = gerentesMap.get((String) cbGerente.getSelectedItem());

            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome do projeto é obrigatório!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Projeto projeto = new Projeto(nome, descricao, dataInicio, dataTermino, status, gerenteId);
            projeto.setId(projetoEditandoId);

            ProjetoDAO projetoDAO = new ProjetoDAO();
            if (projetoDAO.atualizarProjeto(projeto)) {
                JOptionPane.showMessageDialog(this, "Projeto atualizado com sucesso!");
                limparFormulario();
                carregarProjetos();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar projeto!", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Exclui o projeto selecionado
    private void excluirProjeto() {
        int selectedRow = tabelaProjetos.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto para excluir!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int projetoId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String nomeProjeto = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o projeto:\n" + nomeProjeto + "?\n\nEsta ação não pode ser desfeita.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            ProjetoDAO projetoDAO = new ProjetoDAO();
            if (projetoDAO.excluirProjeto(projetoId)) {
                JOptionPane.showMessageDialog(this, "Projeto excluído com sucesso!");
                limparFormulario();
                carregarProjetos();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir projeto!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Abre o frame para gerenciar equipes do projeto selecionado
    private void gerenciarEquipesProjeto() {
        int selectedRow = tabelaProjetos.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto para gerenciar equipes!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int projetoId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String projetoNome = (String) tableModel.getValueAt(selectedRow, 1);

        AlocarEquipesFrame alocarFrame = new AlocarEquipesFrame(projetoId, projetoNome);
        alocarFrame.setVisible(true);
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtDescricao.setText("");
        txtDataInicio.setValue(new Date());
        txtDataTermino.setValue(null);
        cbStatus.setSelectedIndex(0);
        cbGerente.setSelectedIndex(0);
        projetoEditandoId = -1;
        btnSalvar.setText("Salvar");
        btnEditar.setEnabled(true);
        tabelaProjetos.clearSelection();
    }

    // Cancela o projeto selecionado e todas as suas tarefas pendentes
    private void cancelarProjeto() {
        int selectedRow = tabelaProjetos.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto para cancelar!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int projetoId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String nomeProjeto = (String) tableModel.getValueAt(selectedRow, 1);
        String statusAtual = (String) tableModel.getValueAt(selectedRow, 5);

        if ("cancelado".equals(statusAtual)) {
            JOptionPane.showMessageDialog(this, "Este projeto já está cancelado!", "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja cancelar o projeto:\n" + nomeProjeto
                        + "?\n\nEsta ação cancelará todas as tarefas pendentes.",
                "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {

                ProjetoDAO projetoDAO = new ProjetoDAO();
                Projeto projeto = projetoDAO.buscarPorId(projetoId);
                projeto.setStatus("cancelado");
                projetoDAO.atualizarProjeto(projeto);

                TarefaDAO tarefaDAO = new TarefaDAO();
                List<Tarefa> tarefas = tarefaDAO.listarTarefasPorProjeto(projetoId);
                int tarefasCanceladas = 0;

                for (Tarefa tarefa : tarefas) {
                    if ("pendente".equals(tarefa.getStatus()) || "em_execucao".equals(tarefa.getStatus())) {
                        tarefa.setStatus("cancelada");
                        if (tarefaDAO.atualizarTarefa(tarefa)) {
                            tarefasCanceladas++;
                        }
                    }
                }

                JOptionPane.showMessageDialog(this,
                        "Projeto cancelado com sucesso!\n" +
                                tarefasCanceladas + " tarefa(s) pendente(s) foram canceladas automaticamente.",
                        "Cancelamento Concluído",
                        JOptionPane.INFORMATION_MESSAGE);

                JOptionPane.showMessageDialog(this, "Projeto cancelado com sucesso!");
                carregarProjetos();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar projeto: " + e.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}