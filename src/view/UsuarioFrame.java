package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import dao.UsuarioDAO;
import model.Usuario;
import util.Validacao;
import util.Validacao.ResultadoValidacao;

// Tela para cadastro, edição e exclusão de usuários
public class UsuarioFrame extends JFrame {
    private JTextField txtNome, txtCpf, txtEmail, txtCargo, txtLogin, txtSenha;
    private JComboBox<String> cbPerfil;
    private JButton btnSalvar, btnEditar, btnExcluir, btnLimpar;
    private JTable tabelaUsuarios;
    private DefaultTableModel tableModel;
    private int usuarioEditandoId = -1;

    // Construtor
    public UsuarioFrame() {
        setTitle("Cadastro de Usuários");
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel panelForm = new JPanel(new GridLayout(7, 2, 5, 5));

        panelForm.add(new JLabel("Nome Completo:"));
        txtNome = new JTextField();
        panelForm.add(txtNome);

        panelForm.add(new JLabel("CPF:"));
        txtCpf = new JTextField();
        panelForm.add(txtCpf);

        panelForm.add(new JLabel("E-mail:"));
        txtEmail = new JTextField();
        panelForm.add(txtEmail);

        panelForm.add(new JLabel("Cargo:"));
        txtCargo = new JTextField();
        panelForm.add(txtCargo);

        panelForm.add(new JLabel("Login:"));
        txtLogin = new JTextField();
        panelForm.add(txtLogin);

        panelForm.add(new JLabel("Senha:"));
        txtSenha = new JTextField();
        panelForm.add(txtSenha);

        panelForm.add(new JLabel("Perfil:"));
        cbPerfil = new JComboBox<>(new String[] { "administrador", "gerente", "colaborador" });
        panelForm.add(cbPerfil);

        JPanel panelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");

        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnEditar);
        panelBotoes.add(btnExcluir);
        panelBotoes.add(btnLimpar);

        String[] colunas = { "ID", "Nome", "CPF", "E-mail", "Cargo", "Login", "Perfil" };
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaUsuarios = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(panelForm, BorderLayout.NORTH);
        topPanel.add(panelBotoes, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        carregarUsuarios();

        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarUsuario();
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarUsuario();
            }
        });

        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirUsuario();
            }
        });

        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparFormulario();
            }
        });

        tabelaUsuarios.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    preencherFormularioComSelecao();
                }
            }
        });
    }

    // Carrega usuários do banco e exibe na tabela
    private void carregarUsuarios() {
        tableModel.setRowCount(0);
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.listarUsuarios();

        for (Usuario usuario : usuarios) {
            Object[] row = {
                    usuario.getId(),
                    usuario.getNomeCompleto(),
                    usuario.getCpf(),
                    usuario.getEmail(),
                    usuario.getCargo(),
                    usuario.getLogin(),
                    usuario.getPerfil()
            };
            tableModel.addRow(row);
        }
    }

    // Salva ou atualiza usuário
    private void salvarUsuario() {
        String nome = txtNome.getText();
        String cpf = txtCpf.getText();
        String email = txtEmail.getText();
        String cargo = txtCargo.getText();
        String login = txtLogin.getText();
        String senha = txtSenha.getText();
        String perfil = (String) cbPerfil.getSelectedItem();

        if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || cargo.isEmpty() || login.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (usuarioEditandoId == -1 && senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Senha é obrigatória para novo usuário!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ResultadoValidacao resultadoCPF = Validacao.validarCPF(cpf);
        if (!resultadoCPF.isValido()) {
            JOptionPane.showMessageDialog(this, resultadoCPF.getMensagem(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ResultadoValidacao resultadoEmail = Validacao.validarEmail(email);
        if (!resultadoEmail.isValido()) {
            JOptionPane.showMessageDialog(this, resultadoEmail.getMensagem(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario usuario = new Usuario(nome, cpf, email, cargo, login, senha, perfil);
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        boolean sucesso;
        if (usuarioEditandoId == -1) {
            sucesso = usuarioDAO.inserirUsuario(usuario);
        } else {
            usuario.setId(usuarioEditandoId);
            sucesso = usuarioDAO.atualizarUsuario(usuario);
        }

        if (sucesso) {
            String mensagem = usuarioEditandoId == -1 ? "Usuário salvo com sucesso!"
                    : "Usuário atualizado com sucesso!";
            JOptionPane.showMessageDialog(this, mensagem);
            limparFormulario();
            carregarUsuarios();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inicia edição do usuário selecionado
    private void editarUsuario() {
        int selectedRow = tabelaUsuarios.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para editar!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        usuarioEditandoId = (int) tableModel.getValueAt(selectedRow, 0);
        btnSalvar.setText("Atualizar");
        btnEditar.setEnabled(false);

    }

    // Exclui o usuário selecionado
    private void excluirUsuario() {
        int selectedRow = tabelaUsuarios.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para excluir!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nome = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o usuário:\n" + nome + "?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            if (usuarioDAO.excluirUsuario(id)) {
                JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso!");
                limparFormulario();
                carregarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        txtCargo.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        cbPerfil.setSelectedIndex(0);
        usuarioEditandoId = -1;
        btnSalvar.setText("Salvar");
        btnEditar.setEnabled(true);
    }

    private void preencherFormularioComSelecao() {
        int selectedRow = tabelaUsuarios.getSelectedRow();
        if (selectedRow >= 0) {
            txtNome.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtCpf.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtEmail.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtCargo.setText(tableModel.getValueAt(selectedRow, 4).toString());
            txtLogin.setText(tableModel.getValueAt(selectedRow, 5).toString());
            txtSenha.setText("");
            cbPerfil.setSelectedItem(tableModel.getValueAt(selectedRow, 6).toString());
        }
    }
}