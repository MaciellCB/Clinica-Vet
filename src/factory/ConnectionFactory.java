package factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // A URL de conexão com o banco que criamos
    private static final String URL = "jdbc:mysql://localhost:3306/clinica_veterinaria";
    // O usuário padrão do MySQL geralmente é root
    private static final String USER = "root";
    // Coloque a sua senha do MySQL Workbench aqui!
    private static final String PASS = "root";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco de dados: " + e.getMessage());
        }
    }
}