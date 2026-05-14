package dao;

import factory.ConnectionFactory;
import modelo.Raca;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RacaDAO {
    private Connection connection;

    public RacaDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    // Regra: Não permitir raças duplicadas para o mesmo tipo de animal
    private boolean racaExiste(String nomeRaca, String tipoAnimal) {
        String sql = "SELECT id_raca FROM raca WHERE nome_raca = ? AND tipo_animal = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nomeRaca);
            stmt.setString(2, tipoAnimal);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true se encontrou registro
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void cadastrar(Raca r) {
        if (racaExiste(r.getNomeRaca(), r.getTipoAnimal())) {
            throw new RuntimeException("Esta raça já está cadastrada para este tipo de animal.");
        }

        String sql = "INSERT INTO raca (nome_raca, tipo_animal, status) VALUES (?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, r.getNomeRaca());
            stmt.setString(2, r.getTipoAnimal());
            stmt.setBoolean(3, r.isStatus());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar raça: " + e.getMessage());
        }
    }

    public void alterar(Raca r) {
        String sql = "UPDATE raca SET nome_raca=?, tipo_animal=? WHERE id_raca=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, r.getNomeRaca());
            stmt.setString(2, r.getTipoAnimal());
            stmt.setInt(3, r.getIdRaca());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar raça: " + e.getMessage());
        }
    }

    // Regra: Exclusão Lógica [cite: 100, 104, 105]
    public void excluirLogico(int idRaca) {
        String sql = "UPDATE raca SET status = false WHERE id_raca = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idRaca);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir raça: " + e.getMessage());
        }
    }

    public List<Raca> listarAtivas() {
        List<Raca> racas = new ArrayList<>();
        String sql = "SELECT * FROM raca WHERE status = true";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Raca r = new Raca();
                r.setIdRaca(rs.getInt("id_raca"));
                r.setNomeRaca(rs.getString("nome_raca"));
                r.setTipoAnimal(rs.getString("tipo_animal"));
                r.setStatus(rs.getBoolean("status"));
                racas.add(r);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar raças: " + e.getMessage());
        }
        return racas;
    }
}