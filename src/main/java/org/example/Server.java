package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

public class Server {
    public static final String ADDRESS = "localhost";
    public static final int PORT = 4000;
    private ServerSocket serverSocket;
    private final List<SocketClient> clients = new LinkedList<>();

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado na porta: " + PORT);
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException {
        System.out.println("Aguardando conexao de um cliente!");
        while (true) {
            SocketClient clientSocket = new SocketClient(serverSocket.accept());
            clients.add(clientSocket);
            new Thread(() -> clientMessageLoop(clientSocket)).start();
        }
    }

    private void clientMessageLoop(SocketClient clientSocket) {
        String msg;
        try {
            while ((msg = clientSocket.getMessage()) != null) {
                switch(msg) {
                    case "salvar":
                        salvarConteudo(clientSocket);
                        break;
                    case "gravacoes":
                        carregarConteudo();
                }
            }
        } finally {
            clientSocket.close();
        }
    }

    private void carregarConteudo() {
    }

    private void salvarConteudo(SocketClient clientSocket) {
        String msg = clientSocket.getMessage();
        switch(msg) {
            case "id": 
                salvarID();
                break;
            case "local":
                salvarLocal();
                break;
            case "tempoTotal":
                salvarTempoTotal();
                break;
            case "tempoMarcacoes":
                salvarTempoMarcacoes();
                break;
            case "textoMarcacoes":
                salvarTempoMarcacoes();
                break;
            default:
                
        }

    }

    private void salvarTempoMarcacoes() {
    }

    private void salvarTempoTotal() {
    }

    private void salvarLocal() {
    }

    private void salvarID() {
    }

    public static void main(String[] args) throws IOException {
        System.out.println("CONSOLE DO SERVIDOR");
        try {
            Server server = new Server();
            server.start();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar servidor: " + e.getMessage());
        }
        System.out.println("Servidor finalizado!");
    }

}
