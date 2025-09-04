package util;

import java.util.Date;
import java.util.regex.Pattern;

// Classe para validação de dados de entrada
public class Validacao {

    // Padrões regex para validação
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    // Email no formato usuario@dominio.extensao

    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
    // CPF no formato 000.000.000-00

    public static class ResultadoValidacao {
        private boolean valido;
        private String mensagem;

        public ResultadoValidacao(boolean valido, String mensagem) {
            this.valido = valido;
            this.mensagem = mensagem;
        }

        public boolean isValido() {
            return valido;
        }

        public String getMensagem() {
            return mensagem;
        }
    }

    // Valida formato de email
    public static ResultadoValidacao validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ResultadoValidacao(false, "Email é obrigatório");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new ResultadoValidacao(false, "Email inválido");
        }
        return new ResultadoValidacao(true, "");
    }

    // Valida CPF no formato 000.000.000-00
    public static ResultadoValidacao validarCPF(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return new ResultadoValidacao(false, "CPF é obrigatório");
        }
        if (!CPF_PATTERN.matcher(cpf).matches()) {
            return new ResultadoValidacao(false, "CPF deve estar no formato 000.000.000-00");
        }
        return new ResultadoValidacao(true, "");
    }

    // Valida se a data de fim é posterior à data de início
    public static ResultadoValidacao validarDatas(Date inicio, Date fim) {
        if (inicio == null) {
            return new ResultadoValidacao(false, "Data de início é obrigatória");
        }
        if (fim == null) {
            return new ResultadoValidacao(false, "Data de fim é obrigatória");
        }
        if (fim.before(inicio)) {
            return new ResultadoValidacao(false, "Data de fim deve ser posterior à data de início");
        }
        return new ResultadoValidacao(true, "");
    }

    // Valida se um campo obrigatório não está vazio
    public static ResultadoValidacao validarCampoObrigatorio(String valor, String nomeCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            return new ResultadoValidacao(false, nomeCampo + " é obrigatório");
        }
        return new ResultadoValidacao(true, "");
    }
}
