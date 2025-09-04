package view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import dao.UsuarioDAO;
import model.Usuario;
import util.Autenticacao;

// Frame para o login do sistema
public class LoginFrame extends JFrame {
    private JTextField txtLogin;
    private JPasswordField txtSenha;

    // Construtor
    public LoginFrame() {
        setTitle("Login - Sistema de Gestão");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        panel.add(new JLabel("Login:"));
        txtLogin = new JTextField();
        panel.add(txtLogin);

        panel.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        panel.add(txtSenha);

        JButton btnLogin = new JButton("Entrar");
        JButton btnCancelar = new JButton("Cancelar");

        panel.add(btnLogin);
        panel.add(btnCancelar);

        add(panel);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = txtLogin.getText();
                String senha = new String(txtSenha.getPassword());

                if (login.isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                Usuario usuario = usuarioDAO.autenticar(login, senha);

                if (usuario != null) {
                    Autenticacao.setUsuarioLogado(usuario);
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Bem-vindo, " + usuario.getNomeCompleto() + "!",
                            "Login Bem-sucedido", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new MainFrame().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Login ou senha inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    // Método principal para iniciar o aplicativo
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}