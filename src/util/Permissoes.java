package util;

import model.Usuario;

// Classe para gerenciar e verificar permissões com base no perfil do usuário logado
public class Permissoes {

    public static boolean isAdministrador() {
        Usuario usuario = Autenticacao.getUsuarioLogado();
        return usuario != null && "administrador".equals(usuario.getPerfil());
    }

    public static boolean isGerente() {
        Usuario usuario = Autenticacao.getUsuarioLogado();
        return usuario != null && "gerente".equals(usuario.getPerfil());
    }

    public static boolean isColaborador() {
        Usuario usuario = Autenticacao.getUsuarioLogado();
        return usuario != null && "colaborador".equals(usuario.getPerfil());
    }

    public static boolean podeGerenciarUsuarios() {
        return isAdministrador();
    }

    public static boolean podeGerenciarProjetos() {
        return isAdministrador() || isGerente();
    }

    public static boolean podeVerRelatorios() {
        return isAdministrador() || isGerente();
    }
}
