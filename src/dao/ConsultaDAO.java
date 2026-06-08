package dao;

import factory.ConnectionFactory;
import modelo.Animal;
import modelo.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {
    private Connection connection;

    public ConsultaDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    // Busca clientes pelo nome
    public List<Cliente> buscarClientesPorNome(String nome) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nome LIKE ? AND status = true ORDER BY nome";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("id_cliente"));
                    c.setNome(rs.getString("nome"));
                    c.setCpf(rs.getString("cpf"));
                    c.setTelefone(rs.getString("telefone"));
                    if (rs.getDate("data_nascimento") != null)
                        c.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                    clientes.add(c);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar clientes: " + e.getMessage());
        }
        return clientes;
    }

    // Busca clientes pelo CPF
    public List<Cliente> buscarClientesPorCpf(String cpf) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE cpf = ? AND status = true";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("id_cliente"));
                    c.setNome(rs.getString("nome"));
                    c.setCpf(rs.getString("cpf"));
                    c.setTelefone(rs.getString("telefone"));
                    if (rs.getDate("data_nascimento") != null)
                        c.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                    clientes.add(c);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar clientes por CPF: " + e.getMessage());
        }
        return clientes;
    }

    public List<Animal> buscarAnimaisPorCliente(int idCliente) {
        List<Animal> animais = new ArrayList<>();
        String sql = """
                SELECT a.*, r.nome_raca, r.tipo_animal, c.nome AS nome_cliente
                FROM animal a
                JOIN raca    r ON a.id_raca    = r.id_raca
                JOIN cliente c ON a.id_cliente = c.id_cliente
                WHERE a.id_cliente = ? AND a.status = true
                ORDER BY a.nome
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Animal a = new Animal();
                    a.setIdAnimal(rs.getInt("id_animal"));
                    a.setNome(rs.getString("nome"));
                    if (rs.getDate("data_nascimento") != null)
                        a.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                    a.setSexo(rs.getString("sexo"));
                    a.setCor(rs.getString("cor"));
                    a.setObservacoes(rs.getString("observacoes"));
                    a.setIdCliente(rs.getInt("id_cliente"));
                    a.setIdRaca(rs.getInt("id_raca"));
                    a.setStatus(rs.getBoolean("status"));
                    a.setNomeRaca(rs.getString("nome_raca"));
                    a.setNomeCliente(rs.getString("nome_cliente"));
                    animais.add(a);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar animais: " + e.getMessage());
        }
        return animais;
    }
}