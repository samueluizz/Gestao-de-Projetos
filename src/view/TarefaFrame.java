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
import model.Tarefa;
import model.Usuario;
import util.Autenticacao;

// Frame para gerenciar tarefas no sistema
public class TarefaFrame extends JFrame {
    private JTextField txtTitulo, txtDescricao;
    private JComboBox<String> cbStatus, cbProjeto, cbResponsavel;
    private JFormattedTextField txtDataInicio, txtDataPrevista;
    private JButton btnSalvar, btnEditar, btnExcluir, btnLimpar, btnConcluir;
    private JTable tabelaTarefas;
    private DefaultTableModel tableModel;
    private Map<String, Integer> projetosMap = new HashMap<>();
    private Map<String, Integer> usuariosMap = new HashMap<>();
    private int tarefaEditandoId = -1;

    // Construtor
    public TarefaFrame() {
        setTitle("Gestão de Tarefas");
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JPanel panelForm = new JPanel(new GridLayout(7, 2, 5, 5));

        panelForm.add(new JLabel("Título da Tarefa:*"));
        txtTitulo = new JTextField();
        panelForm.add(txtTitulo);

        panelForm.add(new JLabel("Descrição:"));
        txtDescricao = new JTextField();
        panelForm.add(txtDescricao);

        panelForm.add(new JLabel("Projeto:*"));
        cbProjeto = new JComboBox<>();
        carregarProjetos();
        panelForm.add(cbProjeto);

        panelForm.add(new JLabel("Responsável:*"));
        cbResponsavel = new JComboBox<>();
        carregarUsuarios();
        panelForm.add(cbResponsavel);

        panelForm.add(new JLabel("Status:*"));
        cbStatus = new JComboBox<>(new String[] { "pendente", "em_execucao", "concluida" });
        panelForm.add(cbStatus);

        panelForm.add(new JLabel("Data Início:"));
        txtDataInicio = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        panelForm.add(txtDataInicio);

        panelForm.add(new JLabel("Data Prevista:"));
        txtDataPrevista = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        panelForm.add(txtDataPrevista);

        JPanel panelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");
        btnConcluir = new JButton("Concluir Tarefa");

        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnEditar);
        panelBotoes.add(btnExcluir);
        panelBotoes.add(btnLimpar);
        panelBotoes.add(btnConcluir);

        String[] colunas = { "ID", "Título", "Projeto", "Responsável", "Status", "Início", "Prevista", "Conclusão" };
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaTarefas = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelaTarefas);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(panelForm, BorderLayout.NORTH);
        topPanel.add(panelBotoes, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        carregarTarefas();

        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tarefaEditandoId == -1) {
                    salvarTarefa();
                } else {
                    editarTarefa();
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
                excluirTarefa();
            }
        });

        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparFormulario();
            }
        });

        btnConcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                concluirTarefa();
            }
        });

        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnConcluir.setEnabled(false);

        tabelaTarefas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = tabelaTarefas.getSelectedRow() >= 0;

                Usuario usuario = Autenticacao.getUsuarioLogado();
                if (usuario != null && "colaborador".equals(usuario.getPerfil())) {

                    btnConcluir.setEnabled(hasSelection && isTarefaDoUsuarioLogado(tabelaTarefas.getSelectedRow()));
                } else {

                    btnEditar.setEnabled(hasSelection);
                    btnExcluir.setEnabled(hasSelection);
                    btnConcluir.setEnabled(hasSelection);
                }
            }
        });

        configurarPermissoes();
    }

    // Carrega os projetos no combo box
    private void carregarProjetos() {
        cbProjeto.removeAllItems();
        projetosMap.clear();

        ProjetoDAO projetoDAO = new ProjetoDAO();
        var projetos = projetoDAO.listarProjetos();

        for (var projeto : projetos) {
            String display = projeto.getNome() + " (ID: " + projeto.getId() + ")";
            cbProjeto.addItem(display);
            projetosMap.put(display, projeto.getId());
        }
    }

    // Carrega os usuários no combo box
    private void carregarUsuarios() {
        cbResponsavel.removeAllItems();
        usuariosMap.clear();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        var usuarios = usuarioDAO.listarUsuarios();

        if (usuarios.isEmpty()) {
            cbResponsavel.addItem("Nenhum usuário disponível");
            return;
        }

        for (var usuario : usuarios) {
            String display = usuario.getNomeCompleto() + " (" + usuario.getPerfil() + ")";
            cbResponsavel.addItem(display);
            usuariosMap.put(display, usuario.getId());
        }
    }

    // Carrega as tarefas na tabela
    private void carregarTarefas() {
        tableModel.setRowCount(0);
        TarefaDAO tarefaDAO = new TarefaDAO();
        List<Tarefa> tarefas = tarefaDAO.listarTarefas();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        ProjetoDAO projetoDAO = new ProjetoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        for (Tarefa tarefa : tarefas) {
            String nomeProjeto = "N/A";
            String nomeResponsavel = "N/A";

            try {
                var projeto = projetoDAO.buscarPorId(tarefa.getProjetoId());
                if (projeto != null)
                    nomeProjeto = projeto.getNome();

                var usuario = usuarioDAO.buscarPorId(tarefa.getResponsavelId());
                if (usuario != null)
                    nomeResponsavel = usuario.getNomeCompleto();
            } catch (Exception e) {
                System.out.println("Erro ao carregar dados da tarefa: " + e.getMessage());
            }

            Object[] row = {
                    tarefa.getId(),
                    tarefa.getTitulo(),
                    nomeProjeto,
                    nomeResponsavel,
                    tarefa.getStatus(),
                    tarefa.getDataInicio() != null ? sdf.format(tarefa.getDataInicio()) : "N/A",
                    tarefa.getDataFimPrevista() != null ? sdf.format(tarefa.getDataFimPrevista()) : "N/A",
                    tarefa.getDataFimReal() != null ? sdf.format(tarefa.getDataFimReal()) : "N/A"
            };
            tableModel.addRow(row);
        }
    }

    // Verifica se a tarefa selecionada pertence ao usuário logado
    private boolean isTarefaDoUsuarioLogado(int selectedRow) {
        try {
            int tarefaId = (Integer) tableModel.getValueAt(selectedRow, 0);
            TarefaDAO tarefaDAO = new TarefaDAO();
            Tarefa tarefa = tarefaDAO.buscarPorId(tarefaId);

            Usuario usuarioLogado = Autenticacao.getUsuarioLogado();
            return tarefa != null && usuarioLogado != null &&
                    tarefa.getResponsavelId() == usuarioLogado.getId();
        } catch (Exception e) {
            return false;
        }
    }

    // Salva uma nova tarefa no banco de dados
    private void salvarTarefa() {
        try {
            String titulo = txtTitulo.getText().trim();
            String descricao = txtDescricao.getText().trim();
            int projetoId = projetosMap.get((String) cbProjeto.getSelectedItem());
            int responsavelId = usuariosMap.get((String) cbResponsavel.getSelectedItem());
            String status = (String) cbStatus.getSelectedItem();
            Date dataInicio = (Date) txtDataInicio.getValue();
            Date dataFimPrevista = (Date) txtDataPrevista.getValue();

            if (titulo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Título da tarefa é obrigatório!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dataFimPrevista != null && dataInicio != null && dataFimPrevista.before(dataInicio)) {
                JOptionPane.showMessageDialog(this,
                        "Data de término não pode ser anterior à data de início!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Tarefa tarefa = new Tarefa(titulo, descricao, projetoId, responsavelId, status, dataInicio,
                    dataFimPrevista);
            TarefaDAO tarefaDAO = new TarefaDAO();

            if (tarefaDAO.inserirTarefa(tarefa)) {
                JOptionPane.showMessageDialog(this, "Tarefa salva com sucesso!");
                limparFormulario();
                carregarTarefas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar tarefa!", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Prepara o formulário para edição da tarefa selecionada
    private void prepararEdicao() {
        int selectedRow = tabelaTarefas.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para editar!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int tarefaId = (Integer) tableModel.getValueAt(selectedRow, 0);
            TarefaDAO tarefaDAO = new TarefaDAO();
            Tarefa tarefa = tarefaDAO.buscarPorId(tarefaId);

            if (tarefa != null) {
                tarefaEditandoId = tarefaId;
                txtTitulo.setText(tarefa.getTitulo());
                txtDescricao.setText(tarefa.getDescricao());

                ProjetoDAO projetoDAO = new ProjetoDAO();
                var projeto = projetoDAO.buscarPorId(tarefa.getProjetoId());
                if (projeto != null) {
                    String projetoDisplay = projeto.getNome() + " (ID: " + projeto.getId() + ")";
                    cbProjeto.setSelectedItem(projetoDisplay);
                }

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                var usuario = usuarioDAO.buscarPorId(tarefa.getResponsavelId());
                if (usuario != null) {
                    String usuarioDisplay = usuario.getNomeCompleto() + " (" + usuario.getPerfil() + ")";
                    cbResponsavel.setSelectedItem(usuarioDisplay);
                }

                cbStatus.setSelectedItem(tarefa.getStatus());
                txtDataInicio.setValue(tarefa.getDataInicio());
                txtDataPrevista.setValue(tarefa.getDataFimPrevista());

                btnSalvar.setText("Atualizar");
                btnEditar.setEnabled(false);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar tarefa: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Edita a tarefa selecionada
    private void editarTarefa() {
        try {
            String titulo = txtTitulo.getText().trim();
            String descricao = txtDescricao.getText().trim();
            int projetoId = projetosMap.get((String) cbProjeto.getSelectedItem());
            int responsavelId = usuariosMap.get((String) cbResponsavel.getSelectedItem());
            String status = (String) cbStatus.getSelectedItem();
            Date dataInicio = (Date) txtDataInicio.getValue();
            Date dataPrevista = (Date) txtDataPrevista.getValue();

            if (titulo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Título da tarefa é obrigatório!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Tarefa tarefa = new Tarefa(titulo, descricao, projetoId, responsavelId, status, dataInicio, dataPrevista);
            tarefa.setId(tarefaEditandoId);

            TarefaDAO tarefaDAO = new TarefaDAO();
            if (tarefaDAO.atualizarTarefa(tarefa)) {
                JOptionPane.showMessageDialog(this, "Tarefa atualizada com sucesso!");
                limparFormulario();
                carregarTarefas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar tarefa!", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Exclui a tarefa selecionada
    private void excluirTarefa() {
        int selectedRow = tabelaTarefas.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para excluir!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tarefaId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String tituloTarefa = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a tarefa:\n" + tituloTarefa + "?\n\nEsta ação não pode ser desfeita.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            TarefaDAO tarefaDAO = new TarefaDAO();
            if (tarefaDAO.excluirTarefa(tarefaId)) {
                JOptionPane.showMessageDialog(this, "Tarefa excluída com sucesso!");
                limparFormulario();
                carregarTarefas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir tarefa!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Conclui a tarefa selecionada
    private void concluirTarefa() {
        int selectedRow = tabelaTarefas.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para concluir!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tarefaId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String tituloTarefa = (String) tableModel.getValueAt(selectedRow, 1);
        String statusAtual = (String) tableModel.getValueAt(selectedRow, 4);

        if ("concluida".equals(statusAtual)) {
            JOptionPane.showMessageDialog(this, "Esta tarefa já está concluída!", "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja marcar a tarefa como concluída?\n" + tituloTarefa,
                "Concluir Tarefa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            TarefaDAO tarefaDAO = new TarefaDAO();
            if (tarefaDAO.concluirTarefa(tarefaId)) {
                JOptionPane.showMessageDialog(this, "Tarefa concluída com sucesso!");
                carregarTarefas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao concluir tarefa!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Configura as permissões dos botões com base no perfil do usuário logado
    private void configurarPermissoes() {
        Usuario usuario = Autenticacao.getUsuarioLogado();
        if (usuario == null)
            return;

        String perfil = usuario.getPerfil();

        if ("colaborador".equals(perfil)) {

            btnSalvar.setEnabled(false);
            btnEditar.setEnabled(false);
            btnExcluir.setEnabled(false);

        }
    }

    private void limparFormulario() {
        txtTitulo.setText("");
        txtDescricao.setText("");
        cbProjeto.setSelectedIndex(0);
        cbResponsavel.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        txtDataInicio.setValue(null);
        txtDataPrevista.setValue(null);
        tarefaEditandoId = -1;
        btnSalvar.setText("Salvar");
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnConcluir.setEnabled(false);
        tabelaTarefas.clearSelection();
    }

}