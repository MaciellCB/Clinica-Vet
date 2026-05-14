package dao;

import factory.ConnectionFactory;
import modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private Connection connection;

    public ClienteDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    // Regra: Validar CPF
    private boolean isCpfValido(String cpf) {
        if (cpf == null) return false;
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;
        return true;
    }

    public void cadastrar(Cliente c) {
        if (!isCpfValido(c.getCpf())) throw new RuntimeException("CPF Inválido.");
        if (c.getNome() == null || c.getNome().trim().isEmpty()) throw new RuntimeException("Nome é obrigatório."); // [cite: 48]

        String sql = "INSERT INTO cliente (nome, cpf, data_nascimento, telefone, endereco, bairro, cidade, estado, cep, status) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getCpf());
            stmt.setDate(3, c.getDataNascimento() != null ? java.sql.Date.valueOf(c.getDataNascimento()) : null);
            stmt.setString(4, c.getTelefone());
            stmt.setString(5, c.getEndereco());
            stmt.setString(6, c.getBairro());
            stmt.setString(7, c.getCidade());
            stmt.setString(8, c.getEstado());
            stmt.setString(9, c.getCep());
            stmt.setBoolean(10, c.isStatus());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    public void alterar(Cliente c) {
        if (!isCpfValido(c.getCpf())) throw new RuntimeException("CPF Inválido."); // [cite: 95]

        String sql = "UPDATE cliente SET nome=?, cpf=?, data_nascimento=?, telefone=?, endereco=?, bairro=?, cidade=?, estado=?, cep=? WHERE id_cliente=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getCpf());
            stmt.setDate(3, c.getDataNascimento() != null ? java.sql.Date.valueOf(c.getDataNascimento()) : null);
            stmt.setString(4, c.getTelefone());
            stmt.setString(5, c.getEndereco());
            stmt.setString(6, c.getBairro());
            stmt.setString(7, c.getCidade());
            stmt.setString(8, c.getEstado());
            stmt.setString(9, c.getCep());
            stmt.setInt(10, c.getIdCliente());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar cliente: " + e.getMessage());
        }
    }

    // Regra: Exclusão Lógica [cite: 100, 104, 105]
    public void excluirLogico(int idCliente) {
        String sql = "UPDATE cliente SET status = false WHERE id_cliente = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir cliente: " + e.getMessage());
        }
    }

    public List<Cliente> listarAtivos() {
        List<Cliente> clientes = new ArrayList<>();
        // Registros inativos não aparecem nas consultas padrão
        String sql = "SELECT * FROM cliente WHERE status = true";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setNome(rs.getString("nome"));
                c.setCpf(rs.getString("cpf"));
                if (rs.getDate("data_nascimento") != null) {
                    c.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                }
                c.setTelefone(rs.getString("telefone"));

                // As linhas abaixo foram adicionadas para resgatar os dados faltantes
                c.setEndereco(rs.getString("endereco"));
                c.setBairro(rs.getString("bairro"));
                c.setCidade(rs.getString("cidade"));
                c.setEstado(rs.getString("estado"));
                c.setCep(rs.getString("cep"));

                c.setStatus(rs.getBoolean("status"));
                clientes.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes: " + e.getMessage());
        }
        return clientes;
    }
}