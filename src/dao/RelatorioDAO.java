package dao;

import factory.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {
    private Connection connection;

    public RelatorioDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    // cada linha = um animal
    public List<String[]> relatorioClientesEAnimais() {
        List<String[]> linhas = new ArrayList<>();
        String sql = """
                SELECT
                    c.nome        AS nome_cliente,
                    c.cpf,
                    a.nome        AS nome_animal,
                    r.nome_raca,
                    a.data_nascimento
                FROM cliente c
                LEFT JOIN animal a ON c.id_cliente = a.id_cliente AND a.status = true
                LEFT JOIN raca   r ON a.id_raca    = r.id_raca
                WHERE c.status = true
                ORDER BY c.nome, a.nome
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String[] linha = new String[5];
                linha[0] = rs.getString("nome_cliente");
                linha[1] = rs.getString("cpf");
                linha[2] = rs.getString("nome_animal")  != null ? rs.getString("nome_animal")  : "(sem animais)";
                linha[3] = rs.getString("nome_raca")    != null ? rs.getString("nome_raca")    : "-";
                linha[4] = rs.getDate("data_nascimento") != null
                        ? rs.getDate("data_nascimento").toLocalDate().toString()
                        : "-";
                linhas.add(linha);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar relatório: " + e.getMessage());
        }
        return linhas;
    }

    // Animais aniversariantes por mes
    public List<String[]> relatorioAnimaisAniversariantes(int mes) {
        List<String[]> linhas = new ArrayList<>();
        String sql = """
                SELECT
                    a.nome        AS nome_animal,
                    c.nome        AS nome_cliente,
                    c.telefone,
                    a.data_nascimento
                FROM animal a
                JOIN cliente c ON a.id_cliente = c.id_cliente
                WHERE MONTH(a.data_nascimento) = ?
                  AND a.status = true
                  AND c.status = true
                ORDER BY DAY(a.data_nascimento)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mes);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] linha = new String[4];
                    linha[0] = rs.getString("nome_animal");
                    linha[1] = rs.getString("nome_cliente");
                    linha[2] = rs.getString("telefone") != null ? rs.getString("telefone") : "-";
                    linha[3] = rs.getDate("data_nascimento") != null
                            ? rs.getDate("data_nascimento").toLocalDate().toString()
                            : "-";
                    linhas.add(linha);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar relatório: " + e.getMessage());
        }
        return linhas;
    }

    // clientes aniversariantes por mês
    public List<String[]> relatorioClientesAniversariantes(int mes) {
        List<String[]> linhas = new ArrayList<>();
        String sql = """
                SELECT nome, cpf, data_nascimento, telefone
                FROM cliente
                WHERE MONTH(data_nascimento) = ?
                  AND status = true
                ORDER BY DAY(data_nascimento)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mes);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] linha = new String[4];
                    linha[0] = rs.getString("nome");
                    linha[1] = rs.getString("cpf");
                    linha[2] = rs.getDate("data_nascimento") != null
                            ? rs.getDate("data_nascimento").toLocalDate().toString()
                            : "-";
                    linha[3] = rs.getString("telefone") != null ? rs.getString("telefone") : "-";
                    linhas.add(linha);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar relatório: " + e.getMessage());
        }
        return linhas;
    }
}