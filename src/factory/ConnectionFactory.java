package factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // a conexao do banco
    private static final String URL = "jdbc:mysql://localhost:3306/clinica_veterinaria";
    // nome padrao
    private static final String USER = "root";
    // senha padrao(deixei a senha igual o usuario por enquanto manu, se achar bom trocar a gente troca, mas assim me parece mais pratico
    // acho que vou ter que mudar minha senha pra 'root' ent
    private static final String PASS = "root";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco de dados: " + e.getMessage());
        }
    }
}