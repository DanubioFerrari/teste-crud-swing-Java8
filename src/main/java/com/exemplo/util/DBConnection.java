package com.exemplo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/";
    private static final String DATABASE = "crud_swing";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    static {
        try {
            criarBancoDeDados();
            criarTabelaProdutos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DATABASE, USER, PASSWORD);
    }

    private static void criarBancoDeDados() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE DATABASE " + DATABASE;
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            if (e.getSQLState().equals("42P04")) {
                System.out.println("Banco de dados já existe.");
            } else {
                throw e;
            }
        }
    }

    private static void criarTabelaProdutos() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS produtos (" +
                "id SERIAL PRIMARY KEY," +
                "descricao VARCHAR(80) NOT NULL," +
                "valor DECIMAL(10, 2) NOT NULL," +
                "ean13 VARCHAR(13) NOT NULL," +
                "status CHAR(1) NOT NULL," +
                "percentual_imposto DECIMAL(5, 2) NOT NULL," +
                "valor_imposto DECIMAL(10, 2) NOT NULL" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(createTableSQL);


        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
