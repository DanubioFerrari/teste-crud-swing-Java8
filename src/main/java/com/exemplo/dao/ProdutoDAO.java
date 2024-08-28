package com.exemplo.dao;

import com.exemplo.model.Produto;
import com.exemplo.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void salvar(Produto produto) throws SQLException {
        String sql = "INSERT INTO produtos (descricao, valor, ean13, status, percentual_imposto, valor_imposto) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getDescricao());
            stmt.setDouble(2, produto.getValor());
            stmt.setString(3, produto.getEan13());
            stmt.setString(4, produto.getStatus());
            stmt.setDouble(5, produto.getPercentualImposto());
            stmt.setDouble(6, produto.getValorImposto());
            stmt.executeUpdate();
        }
    }

    public List<Produto> listar() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos order by id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setValor(rs.getDouble("valor"));
                produto.setEan13(rs.getString("ean13"));
                produto.setStatus(rs.getString("status"));
                produto.setPercentualImposto(rs.getDouble("percentual_imposto"));
                produto.calcularValorImposto();
                produtos.add(produto);
            }
        }

        return produtos;
    }

    public void atualizar(Produto produto) throws SQLException {
        String sql = "UPDATE produtos SET descricao = ?, valor = ?, ean13 = ?, status = ?, percentual_imposto = ?, valor_imposto = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getDescricao());
            stmt.setDouble(2, produto.getValor());
            stmt.setString(3, produto.getEan13());
            stmt.setString(4, produto.getStatus());
            stmt.setDouble(5, produto.getPercentualImposto());
            stmt.setDouble(6, produto.getValorImposto());
            stmt.setInt(7, produto.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Produto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        Produto produto = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    produto = new Produto();
                    produto.setId(rs.getInt("id"));
                    produto.setDescricao(rs.getString("descricao"));
                    produto.setValor(rs.getDouble("valor"));
                    produto.setEan13(rs.getString("ean13"));
                    produto.setStatus(rs.getString("status"));
                    produto.setPercentualImposto(rs.getDouble("percentual_imposto"));
                    produto.calcularValorImposto(); // Atualizar o valor do imposto com base no percentual
                }
            }
        }

        return produto;
    }
}
