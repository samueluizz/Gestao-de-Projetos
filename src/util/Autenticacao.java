package util;

import model.Usuario;

// Classe para gerenciar a autenticação e o perfil do usuário logado
public class Autenticacao {
    private static Usuario usuarioLogado;

    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static boolean isAdministrador() {
        return usuarioLogado != null && "administrador".equals(usuarioLogado.getPerfil());
    }

    public static boolean isGerente() {
        return usuarioLogado != null && "gerente".equals(usuarioLogado.getPerfil());
    }

    public static boolean isColaborador() {
        return usuarioLogado != null && "colaborador".equals(usuarioLogado.getPerfil());
    }
}