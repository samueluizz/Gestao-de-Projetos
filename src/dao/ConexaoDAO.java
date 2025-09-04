package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDAO {
    // Configurações de conexão com o banco
    private static final String URL = "jdbc:mysql://localhost:3306/gestao_projetos";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Retorna uma conexão ativa com o MySQL.
     * Caso haja falha, retorna null.
     */
    public static Connection getConnection() {
        try {
            // Garante que o driver JDBC do MySQL esteja carregado
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver JDBC 9.4.0 carregado com sucesso!");

            // Cria a conexão com base na URL, usuário e senha
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão com MySQL estabelecida!");
            return conn;

        } catch (ClassNotFoundException e) {
            // Caso o driver não seja encontrado no classpath
            System.err.println("ERRO: Driver MySQL não encontrado!");
            System.err.println("Verifique se o arquivo JAR está na pasta lib/");
            System.err.println("Nome do arquivo: mysql-connector-j-9.4.0.jar");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            // Caso haja erro ao tentar conectar
            System.err.println("ERRO na conexão com MySQL: " + e.getMessage());
            System.err.println("Verifique se o MySQL está rodando no XAMPP");
            e.printStackTrace();
            return null;
        }
    }
}