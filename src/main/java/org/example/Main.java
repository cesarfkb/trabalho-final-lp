package org.example;

public class Main {
    public static void main(String[] args) {
         ConexaoDB.criarBanco();
         CrudDB.criarTable();
         new TelaPrincipal();
//        CrudDB crud = new CrudDB();
//        crud.pegarDadosGravacao(2);
    }
}