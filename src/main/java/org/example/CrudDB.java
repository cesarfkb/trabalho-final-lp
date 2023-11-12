package org.example;

import java.sql.*;
import java.util.ArrayList;

public class CrudDB {
    int id;
    String local;
    int tempototal;
    ArrayList<Integer> tempos;
    ArrayList<String> textos;

    public static void criarTable() {
        Connection conn = ConexaoDB.conectarAoBanco();
        Statement stmt = null;
        String sql = "CREATE TABLE IF NOT EXISTS gravacoes " +
                "(id INT NOT NULL, " +
                "local VARCHAR(150) NOT NULL, " +
                "tempototal INT NOT NULL, " +
                "tempomarcacoes JSON NULL, " +
                "textomarcacoes JSON NULL," +
                "PRIMARY KEY (id))";
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int pegarUltimoID() {
        String sql = "SELECT MAX(id) FROM gravacoes";
        Connection conn = ConexaoDB.conectarAoBanco();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int id = 1;
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e.printStackTrace();
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                    return id;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return 1;
    }

    public void salvarDadosGravacao() {
        Connection conn = ConexaoDB.conectarAoBanco();
        String sql = "INSERT INTO gravacoes(id, local, tempototal, tempomarcacoes, textomarcacoes) VALUES(?,?,?,?,?)";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, String.valueOf(id));
            stmt.setString(2, local);
            stmt.setString(3, String.valueOf(tempototal));

            stmt.setString(4, tempos.toString());

            stmt.setString(5, stringArrayToString(textos));

            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object[] pegarDadosGravacao(int id) {
        Connection conn = ConexaoDB.conectarAoBanco();
        String sql = "SELECT tempototal,tempomarcacoes,textomarcacoes FROM gravacoes WHERE id=?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int tempoTotal = rs.getInt(1);
                String tempos = rs.getString(2);
                String anotacoes = rs.getString(3);
                return new Object[] { tempoTotal, tempos, anotacoes };
            }
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private ArrayList<String> corrigirTexto(ArrayList<String> textos) {
        ArrayList<String> textosCorrigidos = new ArrayList<>();
        for (String texto : textos) {
            texto = texto.replace("\"", "\\\"");
            texto = texto.replace("\\", "\\\\");
            textosCorrigidos.add(texto);
        }
        return textosCorrigidos;
    }

    private String stringArrayToString(ArrayList<String> textos) {
        String textosAjuste = "[";
        for (int i = 0; i < textos.size(); i++) {
            textosAjuste = textosAjuste + "\"";
            textosAjuste = textosAjuste + textos.get(i);
            textosAjuste = textosAjuste + "\"";
            if (i < textos.size() - 1) {
                textosAjuste = textosAjuste + ", ";
            }
        }
        textosAjuste += "]";
        return textosAjuste;
    }
}