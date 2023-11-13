package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class CrudDB {
    int id;
    String local;
    int tempototal;
    ArrayList<Integer> tempos;
    ArrayList<String> textos;
    int optIdioma;

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
        ResultSet rs = null;
        int id = 1;
        try {
            rs = pegarResultados(sql);
            if (rs.next()) {
                id = rs.getInt(1) + 1;
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    public int pegarIdioma(int id) {
        String sql = "SELECT idioma FROM gravacoes WHERE id=" + id;
        ResultSet rs = null;
        try {
            rs = pegarResultados(sql);
            if (rs.next()) {
                optIdioma = rs.getInt(1);
            }
            return optIdioma;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
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
        String sql = "SELECT tempototal,tempomarcacoes,textomarcacoes FROM gravacoes WHERE id=" + id;
        ResultSet rs = pegarResultados(sql);
        try {
            if (rs.next()) {
                int tempoTotal = rs.getInt(1);
                String tempos = rs.getString(2);
                String stringAnotacoes = rs.getString(3);
                int[] temposArray = stringToIntArray(tempos);
                ArrayList<String> anotacoes = stringAnotacoesParaArray(stringAnotacoes, temposArray.length);

                return new Object[] { tempoTotal, temposArray, anotacoes };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private int[] stringToIntArray(String s) {
        String[] array = s.substring(1, s.length() - 1).split(", ");
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            try {
                intArray[i] = Integer.parseInt(array[i]);
            } catch (NumberFormatException e) {
                return new int[] { -1 };
            }
        }
        return intArray;
    }

    private ArrayList<String> stringAnotacoesParaArray(String texto, int lengthTempos) {
        ArrayList<String> anotacoes = new ArrayList<>();
        String textoAjuste = texto.substring(1, texto.length() - 1);
        String[] anotacoesArray = textoAjuste.split(", ");
        if (anotacoesArray.length == lengthTempos) {
            if (!anotacoesArray[0].equals("")) {
                for (String anotacao : anotacoesArray) {
                    anotacoes.add(anotacao.substring(2, anotacao.length() - 2));
                }
            }
            return anotacoes;
        } else {
            for (int i = 0; i < anotacoesArray.length; i++) {
                if (verificaInicio(anotacoesArray[i])) {
                    if (verificaFim(anotacoesArray[i])) {
                        anotacoes.add(anotacoesArray[i].substring(2, anotacoesArray[i].length() - 2));
                    } else {
                        String anotacao = anotacoesArray[i].substring(2);
                        for (int j = i + 1; j < anotacoesArray.length; j++) {
                            if (verificaFim(anotacoesArray[j])) {
                                anotacao += anotacoesArray[j].substring(0, anotacoesArray[j].length() - 2);
                                anotacoes.add(anotacao);
                                i = j;
                                break;
                            } else {
                                anotacao += anotacoesArray[j];
                            }
                        }
                    }
                }
            }
            return anotacoes;
        }
    }

    private boolean verificaInicio(String s) {
        return s.substring(0, 2).equals("\">");
    }

    private boolean verificaFim(String s) {
        return s.substring(s.length() - 2).equals("<\"");
    }

    private String stringArrayToString(ArrayList<String> textos) {
        String textosAjuste = "[";
        for (int i = 0; i < textos.size(); i++) {
            textosAjuste = textosAjuste + "\">" + textos.get(i) + "<\"";
            if (i < textos.size() - 1) {
                textosAjuste = textosAjuste + ", ";
            }
        }
        textosAjuste += "]";
        return textosAjuste;
    }

    private ResultSet pegarResultados(String sql) {
        Connection conn = ConexaoDB.conectarAoBanco();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        try {
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}