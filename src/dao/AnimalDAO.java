package dao;

import factory.ConnectionFactory;
import modelo.Animal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {
    private Connection connection;

    public AnimalDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void cadastrar(Animal a) {
        if (a.getNome() == null || a.getNome().trim().isEmpty())
            throw new RuntimeException("Nome do animal é obrigatório.");
        if (a.getIdCliente() <= 0)
            throw new RuntimeException("Animal deve estar vinculado a um cliente.");
        if (a.getIdRaca() <= 0)
            throw new RuntimeException("Animal deve possuir uma raça cadastrada.");

        String sql = "INSERT INTO animal (nome, data_nascimento, sexo, cor, observacoes, id_cliente, id_raca, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, a.getNome());
            stmt.setDate(2, a.getDataNascimento() != null ? java.sql.Date.valueOf(a.getDataNascimento()) : null);
            stmt.setString(3, a.getSexo());
            stmt.setString(4, a.getCor());
            stmt.setString(5, a.getObservacoes());
            stmt.setInt(6, a.getIdCliente());
            stmt.setInt(7, a.getIdRaca());
            stmt.setBoolean(8, a.isStatus());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar animal: " + e.getMessage());
        }
    }

    public void alterar(Animal a) {
        if (a.getNome() == null || a.getNome().trim().isEmpty())
            throw new RuntimeException("Nome do animal é obrigatório.");

        String sql = "UPDATE animal SET nome=?, data_nascimento=?, sexo=?, cor=?, observacoes=?, id_cliente=?, id_raca=? " +
                "WHERE id_animal=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, a.getNome());
            stmt.setDate(2, a.getDataNascimento() != null ? java.sql.Date.valueOf(a.getDataNascimento()) : null);
            stmt.setString(3, a.getSexo());
            stmt.setString(4, a.getCor());
            stmt.setString(5, a.getObservacoes());
            stmt.setInt(6, a.getIdCliente());
            stmt.setInt(7, a.getIdRaca());
            stmt.setInt(8, a.getIdAnimal());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar animal: " + e.getMessage());
        }
    }

    public void excluirLogico(int idAnimal) {
        String sql = "UPDATE animal SET status = false WHERE id_animal = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idAnimal);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir animal: " + e.getMessage());
        }
    }

    // Lista todos os animais ativos já trazendo nome do cliente e da raça
    public List<Animal> listarAtivos() {
        List<Animal> animais = new ArrayList<>();
        String sql = """
                SELECT a.*, c.nome AS nome_cliente, r.nome_raca, r.tipo_animal
                FROM animal a
                JOIN cliente c ON a.id_cliente = c.id_cliente
                JOIN raca    r ON a.id_raca    = r.id_raca
                WHERE a.status = true
                ORDER BY a.nome
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                animais.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar animais: " + e.getMessage());
        }
        return animais;
    }

    // busca animais de um cliente específico
    public List<Animal> buscarPorCliente(int idCliente) {
        List<Animal> animais = new ArrayList<>();
        String sql = """
                SELECT a.*, c.nome AS nome_cliente, r.nome_raca, r.tipo_animal
                FROM animal a
                JOIN cliente c ON a.id_cliente = c.id_cliente
                JOIN raca    r ON a.id_raca    = r.id_raca
                WHERE a.id_cliente = ? AND a.status = true
                ORDER BY a.nome
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    animais.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar animais do cliente: " + e.getMessage());
        }
        return animais;
    }

    // busca animais aniversariantes de um mês específico
    public List<Animal> buscarAniversariantesPorMes(int mes) {
        List<Animal> animais = new ArrayList<>();
        String sql = """
                SELECT a.*, c.nome AS nome_cliente, r.nome_raca, r.tipo_animal
                FROM animal a
                JOIN cliente c ON a.id_cliente = c.id_cliente
                JOIN raca    r ON a.id_raca    = r.id_raca
                WHERE MONTH(a.data_nascimento) = ? AND a.status = true
                ORDER BY DAY(a.data_nascimento)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mes);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    animais.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar aniversariantes: " + e.getMessage());
        }
        return animais;
    }

    private Animal mapear(ResultSet rs) throws SQLException {
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

        a.setNomeCliente(rs.getString("nome_cliente"));
        a.setNomeRaca(rs.getString("nome_raca"));
        return a;
    }
}