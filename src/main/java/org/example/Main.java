package org.example;

public class Main {
    public static void main(String[] args) {
         ConexaoDB.criarBanco();
         CrudDB.criarTable();
         TelaPrincipal tp = new TelaPrincipal();
    }
}