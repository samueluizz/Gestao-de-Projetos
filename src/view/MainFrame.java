package view;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.Usuario;
import util.Autenticacao;
import util.Permissoes;

// Frame principal do sistema
public class MainFrame extends JFrame {
    private JMenuBar menuBar;
    private JMenu menuCadastros, menuRelatorios;
    private JMenuItem itemUsuario, itemProjeto, itemEquipe, itemTarefa;

    // Construtor
    public MainFrame() {
        setTitle("Sistema de Gestão de Projetos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Usuario usuario = Autenticacao.getUsuarioLogado();

        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel lblWelcome = new JLabel("Bem-vindo, " + usuario.getNomeCompleto() +
                " (" + usuario.getPerfil() + ")", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcomePanel.add(lblWelcome, BorderLayout.CENTER);

        criarMenu();

        add(welcomePanel);
    }

    // Cria o menu da aplicação
    private void criarMenu() {
        menuBar = new JMenuBar();

        menuCadastros = new JMenu("Cadastros");
        itemUsuario = new JMenuItem("Usuários");
        itemProjeto = new JMenuItem("Projetos");
        itemEquipe = new JMenuItem("Equipes");
        itemTarefa = new JMenuItem("Tarefas");

        menuCadastros.add(itemUsuario);
        menuCadastros.add(itemProjeto);
        menuCadastros.add(itemEquipe);
        menuCadastros.add(itemTarefa);

        menuRelatorios = new JMenu("Relatórios");
        JMenuItem itemDashboard = new JMenuItem("Dashboard");
        JMenuItem itemDesempenho = new JMenuItem("Desempenho");
        JMenuItem itemRiscoAtraso = new JMenuItem("Risco de Atraso");

        menuRelatorios.add(itemDashboard);
        menuRelatorios.add(itemDesempenho);
        menuRelatorios.add(itemRiscoAtraso);

        menuBar.add(menuCadastros);
        menuBar.add(menuRelatorios);

        setJMenuBar(menuBar);

        configurarPermissoes();

        itemUsuario.addActionListener(e -> {
            if (Permissoes.podeGerenciarUsuarios()) {
                new UsuarioFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Apenas administradores podem gerenciar usuários.",
                        "Acesso Negado", JOptionPane.WARNING_MESSAGE);
            }
        });

        itemProjeto.addActionListener(e -> {
            new ProjetoFrame().setVisible(true);
        });

        itemEquipe.addActionListener(e -> {
            new EquipeFrame().setVisible(true);
        });

        itemTarefa.addActionListener(e -> {
            new TarefaFrame().setVisible(true);
        });

        itemDashboard.addActionListener(e -> {
            if (Permissoes.podeVerRelatorios()) {
                new DashboardFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Apenas administradores e gerentes podem ver relatórios.",
                        "Acesso Negado", JOptionPane.WARNING_MESSAGE);
            }
        });

        itemDesempenho.addActionListener(e -> {
            if (Permissoes.podeVerRelatorios()) {
                new RelatorioDesempenhoFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Apenas administradores e gerentes podem ver relatórios.",
                        "Acesso Negado", JOptionPane.WARNING_MESSAGE);
            }
        });

        itemRiscoAtraso.addActionListener(e -> {
            if (Permissoes.podeVerRelatorios()) {
                new RelatorioRiscoAtrasoFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Apenas administradores e gerentes podem ver relatórios.",
                        "Acesso Negado", JOptionPane.WARNING_MESSAGE);
            }
        });

    }

    // Configura as permissões de acesso com base no perfil do usuário
    private void configurarPermissoes() {
        Usuario usuario = Autenticacao.getUsuarioLogado();
        if (usuario == null)
            return;

        String perfil = usuario.getPerfil();

        if ("colaborador".equals(perfil)) {

            itemUsuario.setEnabled(false);
            itemProjeto.setEnabled(false);
            itemEquipe.setEnabled(false);

            menuRelatorios.setVisible(false);
        } else if ("gerente".equals(perfil)) {
            itemUsuario.setEnabled(false);
        }

    }
}